package ru.blackmirrror.todo.presentation.utils

import android.content.Context
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.data.api.NetworkState
import ru.blackmirrror.todo.data.local.TodoItemDb

class DataUpdateWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val repository: TodoRepository = TodoRepository(
        Room.databaseBuilder(context, TodoItemDb::class.java, "todo_items_db")
        .build(), SharedPrefs(context))

    private val sharedPrefs = SharedPrefs(context)

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