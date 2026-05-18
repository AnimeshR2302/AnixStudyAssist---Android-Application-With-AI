package com.anix.android.anixstudyassist.ui.navigation

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SessionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.anix.android.ACTION_EXTEND_SESSION") {
            Log.d("ANIX_SessionReceiver", "Action received: Extend Session")
            sessionManager.extendSession()

            // Clear the notification
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1001)

            Toast.makeText(context, "You are logged in for another 30 mins", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
