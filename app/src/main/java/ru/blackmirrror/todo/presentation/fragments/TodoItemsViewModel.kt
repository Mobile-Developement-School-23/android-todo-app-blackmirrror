package ru.blackmirrror.todo.presentation.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.presentation.utils.locale

class TodoItemsViewModel: ViewModel() {

//    private val repository = TodoRepository(
//        Room.databaseBuilder(context, TodoItemDb::class.java, "todo_items_db")
//        .build(), context)

    private val repository: TodoRepository = locale()

    private val _tasks = MutableSharedFlow<List<TodoItem>>()
    val tasks: SharedFlow<List<TodoItem>> = _tasks.asSharedFlow()

    val countCompletedTask: Flow<Int> = _tasks.map { it -> it.count { it.isDone } }

    init {
        initData()
    }

    fun initData() {
        viewModelScope.launch {
            fetchTodoList()
        }
    }

    private suspend fun fetchTodoList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchTodoList()
            _tasks.emit(repository.getAllTodoItemsNoFlow())
        }
    }

    fun createTask(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createTask(todoItem)
            _tasks.emit(repository.getAllTodoItemsNoFlow())
        }
    }

    fun updateTask(newItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(newItem)
            _tasks.emit(repository.getAllTodoItemsNoFlow())
        }
    }

    fun deleteTask(todoItem: TodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(todoItem)
            _tasks.emit(repository.getAllTodoItemsNoFlow())
        }
    }
}