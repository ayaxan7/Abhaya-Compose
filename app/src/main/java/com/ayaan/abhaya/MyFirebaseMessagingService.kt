package com.ayaan.abhaya

import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.net.toUri
import okhttp3.internal.notify

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM"
        private val CHANNEL_ID = "custom_alert_${System.currentTimeMillis()}"
        private const val CHANNEL_NAME = "Default Channel"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        val message = data["message"] ?: "Unknown Message"
        val soundName = data["sound"] ?: "default_sound"
        val iconName = data["icon"] ?: "ic_default_alert"
        Log.d(TAG, "Message received: $message, Sound: $soundName, Icon: $iconName")
        sendNotification(message, soundName, iconName)
    }
    private fun sendNotification(message: String, soundName: String, iconName: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val soundUri = ("android.resource://$packageName/raw/$soundName").toUri()
//        MediaPlayer.create(this, R.raw.morse_sos_93449).start()

        val iconResId = resources.getIdentifier("baseline_add_alert_24", "drawable", packageName)
        Log.d("IconCheck", "Icon ID: $iconResId")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(
                    soundUri, AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(iconResId)
            .setContentTitle("Unsafe Alert!")
            .setContentText(message)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(0, notification)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "New token: $token")
        // Send the token to your backend server
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        // Implement logic to send the token to your backend
        Log.d(TAG, "Token sent to server: $token")
    }
}