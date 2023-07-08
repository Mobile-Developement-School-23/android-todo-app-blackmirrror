package ru.blackmirrror.todo.presentation.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.data.api.NetworkState

/**
 * Worker to load and update data
 */

@HiltWorker
class DataUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: TodoRepository,
    private val sharedPrefs: SharedPrefs) : Worker(context, workerParams) {

    override fun doWork(): Result {
        initData()
        return Result.success()
    }

    private fun initData() = runBlocking {
        when (val response = repository.getRemoteTasks()) {
            is NetworkState.Success -> {
                sharedPrefs.putRevision(response.data.revision)
                repository.mergeList(response.data.list.map { it.fromApiToTodoItem() })
                repository.updateRemoteTasks(repository.getAllTodoItemsNoFlow())
            }
            is NetworkState.Error -> {
                println("Internet error ${response.response.code()}")
            }
        }
    }
}
