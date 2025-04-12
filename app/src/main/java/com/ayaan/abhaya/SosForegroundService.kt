package com.ayaan.abhaya

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import com.ayaan.abhaya.viewmodels.HomeViewModel

class SosForegroundService : Service() {
    companion object {
        private const val CHANNEL_ID = "sos_channel"
        private const val NOTIFICATION_ID = 1
    }

    private lateinit var mediaSession: MediaSession
    private var pressCount = 0
    private var lastPressTime = 0L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SOSService", "Service started")

        startForegroundServiceNotification()

        val sharedPrefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val name = sharedPrefs.getString("name", "Unknown")
        val number = sharedPrefs.getString("number", "0000000000")

        Log.d("SOSService", "Fetched from SharedPrefs -> Name: $name, Number: $number")

        getCurrentLocation { location ->
            Log.d("SOSService", "Location fetched: Lat=${location.latitude}, Long=${location.longitude}")
            sendSos(name ?: "", number ?: "", location)
        }

//        setupMediaSession()

        return START_NOT_STICKY
    }

    private fun startForegroundServiceNotification() {
        Log.d("SOSService", "Starting foreground notification")
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SOS Service Running")
            .setContentText("Sos Sending in progress...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification() {
        Log.d("SOSService", "Creating SOS triggered notification")
        val channelId = "sos_channel"
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sending SOS")
            .setContentText("SOS triggered via volume button")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notification)
    }

    private fun stopSosService() {
        Log.d("SOSService", "Stopping foreground service")
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun sendSos(name: String, number: String, location: Location) {
        Log.d("SOSService", "Sending SOS via ViewModel with data: Name=$name, Phone=$number, Lat=${location.latitude}, Long=${location.longitude}")
        val viewModel = HomeViewModel()
        try {
            viewModel.sendSos(
                name = name,
                phoneNo = number,
                longitude = location.longitude,
                latitude = location.latitude,
            )
            createNotification()
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
//            stopSosService()
        }
    }

    private fun getCurrentLocation(callback: (Location) -> Unit) {
        Log.d("SOSService", "Fetching current location...")
        val locationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    Log.d("SOSService", "Location success: ${it.latitude}, ${it.longitude}")
                    callback(it)
                } else {
                    Log.d("SOSService", "Location is null")
                }
            }.addOnFailureListener {
                Log.e("SOSService", "Failed to get location", it)
            }
        } else {
            Log.e("SOSService", "Location permission not granted")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
