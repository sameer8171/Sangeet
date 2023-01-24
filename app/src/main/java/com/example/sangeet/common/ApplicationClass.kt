package com.example.sangeet.common

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.sangeet.common.Constants.CHANNEL_ID

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, "Now paying Song!",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "This is a important for song!!"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}