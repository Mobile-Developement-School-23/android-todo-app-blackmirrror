package ru.blackmirrror.todo.presentation.fragments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoItemsViewModel::class.java)) {
            return TodoItemsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}