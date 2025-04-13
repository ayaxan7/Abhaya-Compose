package com.ayaan.abhaya

import android.accessibilityservice.AccessibilityService
import android.content.pm.PackageManager
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import android.util.Log
import androidx.core.app.ActivityCompat
import com.ayaan.abhaya.viewmodels.HomeViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*

class VolumeButtonAccessibilityService : AccessibilityService() {

    private var volumeDownCount = 0
    private var lastPressTime = 0L
    private val pressTimeout = 1500L // Max allowed time between presses (ms)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not needed
    }

    override fun onInterrupt() {
        // Not needed
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastPressTime <= pressTimeout) {
                        volumeDownCount++
                    } else {
                        volumeDownCount = 1
                    }
                    lastPressTime = currentTime

                    Log.d("VolumeService", "Volume DOWN Pressed $volumeDownCount times")
                    Toast.makeText(
                        this,
                        "Volume DOWN Pressed $volumeDownCount times",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (volumeDownCount == 3) {
                        volumeDownCount = 0
                        sos()
                    }
                }
            }
        }
        return super.onKeyEvent(event)
    }

    private fun sos() {
        // Trigger your SOS logic here
        Log.d("VolumeService", "SOS Triggered!")
        Toast.makeText(this, "🚨 SOS Triggered! 🚨", Toast.LENGTH_LONG).show()
        val homeViewModel = HomeViewModel()
        homeViewModel.fetchUserData()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Step 1: Fetch location
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                // Step 2: Observe user data
                homeViewModel.fetchUserData()
                val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
                serviceScope.launch {
                    homeViewModel.userData.collect { userData ->
                        if (userData != null) {
                            homeViewModel.sendSos(
                                latitude,
                                longitude,
                                userData.name,
                                userData.phoneNo
                            )
                            Toast.makeText(this@VolumeButtonAccessibilityService, "🚨 SOS Sent!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        volumeDownCount = 0
        lastPressTime = 0L
        Log.d("VolumeService", "Service destroyed")
    }
}
