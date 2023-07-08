package ru.blackmirrror.todo.presentation.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.blackmirrror.todo.data.TodoRepository

class ViewModelFactoryImpl(private val repository: TodoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoItemsViewModel::class.java)) {
            return TodoItemsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}