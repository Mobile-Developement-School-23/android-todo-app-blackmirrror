package ru.blackmirrror.todo.presentation.utils.notification

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.presentation.utils.DataUpdateWorker
import javax.inject.Inject

class NotificationWorkerFactory @Inject constructor(
    private val repository: TodoRepository,
    private val sharedPrefs: SharedPrefs
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = NotificationWorker(appContext, workerParameters, repository, sharedPrefs)
}