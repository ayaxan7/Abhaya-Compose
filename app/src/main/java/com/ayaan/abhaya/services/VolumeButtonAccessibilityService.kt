package com.ayaan.abhaya.services

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

    private val sequence = mutableListOf<Int>()
    private val volumeSequence = listOf(
        KeyEvent.KEYCODE_VOLUME_DOWN,
        KeyEvent.KEYCODE_VOLUME_UP,
        KeyEvent.KEYCODE_VOLUME_DOWN,
        KeyEvent.KEYCODE_VOLUME_UP
    )
    private val sequenceTimeout = 3000L // 3 seconds to complete the sequence
    private var lastEventTime = 0L

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val homeViewModel = HomeViewModel()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used
    }

    override fun onInterrupt() {
        // Not used
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return super.onKeyEvent(event)

        val currentTime = System.currentTimeMillis()

        if (currentTime - lastEventTime > sequenceTimeout) {
            sequence.clear()
        }

        lastEventTime = currentTime
        sequence.add(event.keyCode)

        // Keep only the latest N entries (length of sequence)
        if (sequence.size > volumeSequence.size) {
            sequence.removeAt(0)
        }

        if (sequence == volumeSequence) {
            sequence.clear()
            Log.d("VolumeService", "Custom volume sequence detected")
            Toast.makeText(this, "🚨 SOS Triggered! 🚨", Toast.LENGTH_SHORT).show()
            sos()
        }

        return super.onKeyEvent(event)
    }

    private fun sos() {
        Log.d("VolumeService", "SOS Triggered!")
        Toast.makeText(this, "🚨 SOS Triggered! 🚨", Toast.LENGTH_LONG).show()

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                serviceScope.launch {
                    homeViewModel.fetchUserData() // make sure this sets `userData` Flow correctly
                    homeViewModel.userData.collect { userData ->
                        if (userData != null) {
                            homeViewModel.sendSos(
                                latitude,
                                longitude,
                                userData.name,
                                userData.phoneNo
                            )
                            Toast.makeText(
                                this@VolumeButtonAccessibilityService,
                                "🚨 SOS Sent!",
                                Toast.LENGTH_SHORT
                            ).show()
                            cancel() // Cancel the coroutine after SOS is sent
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
        serviceScope.cancel()
        Log.d("VolumeService", "Service destroyed")
    }
}
