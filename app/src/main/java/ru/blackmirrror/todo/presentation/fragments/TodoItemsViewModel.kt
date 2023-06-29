package ru.blackmirrror.todo.presentation.fragments

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.data.api.NetworkState
import ru.blackmirrror.todo.data.api.TodoRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TodoItemsViewModel: ViewModel() {

    private val repository = TodoRepository()

    private var _revision: Int = 0

    private val _tasks = MutableSharedFlow<List<TodoItem>>()
    val tasks: SharedFlow<List<TodoItem>> = _tasks.asSharedFlow()

    val countCompletedTask: Flow<Int> = _tasks.map { it -> it.count { it.isDone } }

    private val _oneTask = MutableSharedFlow<TodoItem>()
    val oneTask: SharedFlow<TodoItem> = _oneTask.asSharedFlow()

    init {
        viewModelScope.launch {
            fetchTodoList()
        }
    }

    suspend fun getTodoItemById(currentId: String): TodoItem? = suspendCoroutine { continuation ->
        viewModelScope.launch {
            val todoList = tasks.first()
            val todoItem = todoList.find { it.id == currentId }
            Log.d("API", "getTodoItemById: $todoItem")
            continuation.resume(todoItem)
        }
    }

    private suspend fun fetchTodoList() {
        val wait = viewModelScope.async(Dispatchers.IO) {
                run {
                    when (val response = repository.getRemoteTasks()) {
                        //is NetworkState.Success -> repository.makeMerge(response.data.list.map { it.toToDoItem() })
                        is NetworkState.Success -> {
                            _tasks.emit(response.data.list.map { it.toToDoItem() })
                            _revision = response.data.revision
                        }
                        is NetworkState.Error -> println("Internet error ${response.response.code()}")
                    }
                }
            }

            wait.await()
        //_tasks.emitAll(repository.getRemoteTasks())
    }

    fun createTask(todoItem: TodoItem, context: Context) {
        //if (hasInternetConnection(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = repository.createRemoteOneTask(todoItem, _revision)) {
                    is NetworkState.Success -> fetchTodoList()
                    is NetworkState.Error -> println("Internet error ${response.response.code()}")
                }
            }
        //} else {
//            _loadingState.value = LoadingState.Error("No internet(")
        //}

//        viewModelScope.launch(Dispatchers.IO) {
//            repository.createItem(todoItem)
//        }
    }

    fun updateTask(newItem: TodoItem, context: Context) {
        //if (hasInternetConnection(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = repository.updateRemoteOneTask(newItem, _revision)) {
                    is NetworkState.Success -> fetchTodoList()
                    is NetworkState.Error -> println("Internet error ${response.response.code()}")
                }
            }
        //} else {
//            _loadingState.value = LoadingState.Error("No internet(")
        //}

//        viewModelScope.launch(Dispatchers.IO) {
//            repository.updateToDoItem(newItem)
//        }
    }

    fun deleteTask(todoItem: TodoItem, context: Context) {
        //if (hasInternetConnection(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                when (val response = repository.deleteRemoteOneTask(todoItem, _revision)) {
                    is NetworkState.Success -> fetchTodoList()
                    is NetworkState.Error -> println("Internet error ${response.response.code()}")
                }
            }
        //} else {
//            _loadingState.value = LoadingState.Error("No internet(")
        //}

//        viewModelScope.launch(Dispatchers.IO) {
//            repository.deleteToDoItem(todoItem)
//        }
    }
}