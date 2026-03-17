package com.dailyshayari

import android.app.Application
import androidx.work.*
import com.google.android.gms.ads.MobileAds
import com.dailyshayari.worker.ShayariNotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailyShayariApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        MobileAds.initialize(this) {}
        
        scheduleDailyNotification()
        
        // TEST: Trigger a notification 5 seconds after app start
        triggerTestNotification()
    }

    private fun triggerTestNotification() {
        val workManager = WorkManager.getInstance(this)
        val testWorkRequest = OneTimeWorkRequestBuilder<ShayariNotificationWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()
        workManager.enqueue(testWorkRequest)
    }

    private fun scheduleDailyNotification() {
        val workManager = WorkManager.getInstance(this)
        
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val initialDelay = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<ShayariNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_shayari_notification",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}
