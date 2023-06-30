package ru.blackmirrror.todo.presentation.fragments

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.data.api.NetworkState
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.data.api.NetworkUtils
import ru.blackmirrror.todo.data.local.TodoItemDb
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TodoItemsViewModel(val context: Context): ViewModel() {

    private val repository = TodoRepository(
        Room.databaseBuilder(context, TodoItemDb::class.java, "todo_items_db")
        .build())
    private val sharedPrefs: SharedPrefs = SharedPrefs(context)

    private val _tasks = MutableSharedFlow<List<TodoItem>>()
    val tasks: SharedFlow<List<TodoItem>> = _tasks.asSharedFlow()

    val countCompletedTask: Flow<Int> = _tasks.map { it -> it.count { it.isDone } }

    init {
        if (!NetworkUtils.isInternetConnected(context))
            toast("Ваше устройство не подключено к интернету, данные будут сохранены в оффлайн-режиме")
        viewModelScope.launch {
            fetchTodoList()
        }
    }

    suspend fun getTodoItemById(currentId: String): TodoItem? = suspendCoroutine { continuation ->
        viewModelScope.launch {
            val todoList = tasks.first()
            val todoItem = todoList.find { it.id == currentId }
            continuation.resume(todoItem)
        }
    }

    fun initData() {
        viewModelScope.launch {
            fetchTodoList()
        }
    }

    private suspend fun fetchTodoList() {
        if (NetworkUtils.isInternetConnected(context)) {
            val wait = viewModelScope.async(Dispatchers.IO) {
                run {
                    when (val response = repository.getRemoteTasks()) {
                        is NetworkState.Success -> {
                            repository.initList(response.data.list.map { it.fromApiToTodoItem() }, sharedPrefs.getRevision())
                           _tasks.emit(repository.getAllTodoItemsNoFlow())
                            sharedPrefs.putRevision(response.data.revision)
                            Log.d("REVISION", "fetchTodoList: ${sharedPrefs.getRevision()}")
                        }
                        is NetworkState.Error -> {
                            _tasks.emit(repository.getAllTodoItemsNoFlow())
                            println("Internet error ${response.response.code()}")
                        }
                    }
                }
            }
            wait.await()
        }
        else {
            val result = withContext(Dispatchers.IO) {
                repository.getAllTodoItemsNoFlow()
            }
            _tasks.emit(result)
        }
    }

    fun createTask(todoItem: TodoItem) {
        if (NetworkUtils.isInternetConnected(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = repository.createRemoteOneTask(todoItem, sharedPrefs.getRevision())) {
                    is NetworkState.Success -> println("create")
                    is NetworkState.Error -> println("Internet error ${response.response.code()}")
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.createTodoItem(todoItem)
            _tasks.emit(repository.getAllTodoItemsNoFlow())
        }
    }

    fun updateTask(newItem: TodoItem) {
        if (NetworkUtils.isInternetConnected(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = repository.updateRemoteOneTask(newItem, sharedPrefs.getRevision())) {
                    is NetworkState.Success -> println("update")
                    is NetworkState.Error -> println("Internet error ${response.response.code()}")
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTodoItem(newItem)
            _tasks.emit(repository.getAllTodoItemsNoFlow())
        }
    }

    fun deleteTask(todoItem: TodoItem) {
        if (NetworkUtils.isInternetConnected(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = repository.deleteRemoteOneTask(todoItem, sharedPrefs.getRevision())) {
                    is NetworkState.Success -> println("Delete")
                    is NetworkState.Error -> println("Internet error ${response.response.code()}")
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTodoItem(todoItem)
            _tasks.emit(repository.getAllTodoItemsNoFlow())
        }
    }

    private fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}