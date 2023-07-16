package ru.blackmirrror.todo.presentation.utils.notification

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class NotificationInitializer(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    init {
        enqueueNotificationWorker()
    }

    private fun enqueueNotificationWorker() {

        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .build()

        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(notificationWorkRequest).result.addListener(
            {
                Log.d("NOTIFY", "WorkManager result: Success")
            }, Executors.newSingleThreadExecutor())
    }

    companion object {
        private const val REPEAT_INTERVAL: Long = 8
    }
}