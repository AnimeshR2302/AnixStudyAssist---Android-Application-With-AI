package com.anix.android.anixstudyassist.ui.navigation

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "anix_session_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EXPIRY_TIME = "expiry_time"
        private const val SESSION_DURATION_MINUTES = 30L
        private const val NOTIFICATION_OFFSET_MINUTES = 25L
        private const val TAG = "ANIX_SessionManager"
        private const val WORK_NAME = "SessionExpiryNotification"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun startSession(userId: String) {
        Log.d(TAG, "Starting session for user: $userId")
        val expiryTime =
            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(SESSION_DURATION_MINUTES)
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putLong(KEY_EXPIRY_TIME, expiryTime)
            .apply()
        scheduleNotification()
    }

    fun extendSession() {
        val userId = getUserId()
        if (userId != null) {
            Log.d(TAG, "Extending session for user: $userId")
            val expiryTime =
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(SESSION_DURATION_MINUTES)
            prefs.edit()
                .putLong(KEY_EXPIRY_TIME, expiryTime)
                .apply()
            scheduleNotification()
        }
    }

    fun clearSession() {
        Log.d(TAG, "Clearing session")
        prefs.edit().clear().apply()
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    fun isSessionValid(): Boolean {
        val expiryTime = prefs.getLong(KEY_EXPIRY_TIME, 0)
        val isValid = expiryTime > System.currentTimeMillis()
        Log.d(
            TAG,
            "isSessionValid: $isValid (Expiry: $expiryTime, Current: ${System.currentTimeMillis()})"
        )
        return isValid
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    private fun scheduleNotification() {
        Log.d(TAG, "Scheduling session expiry notification worker")
        val workRequest = OneTimeWorkRequestBuilder<SessionWorker>()
            .setInitialDelay(NOTIFICATION_OFFSET_MINUTES, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}
