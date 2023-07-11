package ru.blackmirrror.todo.presentation.utils

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.data.TodoRepository
import javax.inject.Inject

/**
 * Factory for successful DI in worker
 */

class DataUpdateWorkerFactory @Inject constructor(
    private val repository: TodoRepository,
    private val sharedPrefs: SharedPrefs
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = DataUpdateWorker(appContext, workerParameters, repository, sharedPrefs)
}
