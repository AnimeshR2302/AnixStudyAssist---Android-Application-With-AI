package com.anix.android.anixstudyassist

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.anix.android.anixstudyassist.ui.navigation.SessionWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AnixStudyAssistApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Session Alerts"
        val descriptionText = "Notifications for session expiry and extensions"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(SessionWorker.CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
