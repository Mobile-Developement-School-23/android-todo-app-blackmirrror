package ru.blackmirrror.todo.presentation.utils

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DataUpdateInitializer(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    init {
        enqueueDataUpdateWorker()
    }

    private fun enqueueDataUpdateWorker() {
        val dataUpdateWorkerRequest =
            PeriodicWorkRequestBuilder<DataUpdateWorker>(8, TimeUnit.HOURS)
            .build()

        workManager.enqueue(dataUpdateWorkerRequest).result.addListener(
            {
                Log.d("WorkManager", "WorkManager result: Success")
            }, Executors.newSingleThreadExecutor())
    }
}