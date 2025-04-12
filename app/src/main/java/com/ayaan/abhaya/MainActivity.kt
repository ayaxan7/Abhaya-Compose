package com.ayaan.abhaya

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ayaan.abhaya.ui.theme.AbhayaComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
        setContent {
            AbhayaComposeTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigator(innerPadding)
                }
            }
        }
//        val intent = Intent(this, SosForegroundService::class.java)
//        startForegroundService(intent)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            handleVolumePress()
            return true // prevent system volume change if needed
        }
        return super.onKeyDown(keyCode, event)
    }

    private var lastPressTime = 0L
    private var pressCount = 0

    private fun handleVolumePress() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPressTime < 2000) {
            pressCount++
        } else {
            pressCount = 1
        }
        lastPressTime = currentTime

        if (pressCount == 3) {
            val intent = Intent(this, SosForegroundService::class.java)
            startForegroundService(intent)
            pressCount = 0
        }
    }

}

