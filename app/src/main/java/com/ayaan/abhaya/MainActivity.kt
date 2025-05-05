package com.ayaan.abhaya

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ayaan.abhaya.navigation.Navigator
import com.ayaan.abhaya.ui.theme.AbhayaComposeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.requestAudioFocus(
            null,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        // Fetch FCM Token
//        FirebaseMessaging.getInstance().getToken()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val fcmToken = task.result
//                    val user = FirebaseAuth.getInstance().currentUser
//                    val uid = user?.uid
//
//                    // Log the UID and FCM token after fetching them
//                    Log.d("MainActivity", "User: $uid")
//                    Log.d("MainActivity", "FCM Token: $fcmToken")
//
//                    // You can now send the fcmToken to your server or use it as needed
//                } else {
//                    Log.w("MainActivity", "Fetching FCM token failed", task.exception)
//                }
//            }
        setContent {
            AbhayaComposeTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigator(innerPadding)
                }
            }
        }
    }
}
