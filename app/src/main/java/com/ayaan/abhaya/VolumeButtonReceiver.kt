//package com.ayaan.abhaya
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//
//class VolumeButtonReceiver : BroadcastReceiver() {
//    private var pressCount = 0
//    private var lastPressTime = 0L
//
//    override fun onReceive(context: Context, intent: Intent?) {
//        if (intent?.action == Intent.ACTION_MEDIA_BUTTON) {
//            Log.d("VolumeButtonReceiver", "Volume button pressed ${intent.action}")
//            val keyCode = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1)
//            val currentTime = System.currentTimeMillis()
//
//            if (currentTime - lastPressTime < 2000) {
//                pressCount++
//            } else {
//                pressCount = 1
//            }
//            lastPressTime = currentTime
//
//            if (pressCount == 3) {
//                val serviceIntent = Intent(context, SosForegroundService::class.java)
//                context.startForegroundService(serviceIntent)
//                pressCount = 0
//            }
//        }
//    }
//}