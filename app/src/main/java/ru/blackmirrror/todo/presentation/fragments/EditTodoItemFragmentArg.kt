package ru.blackmirrror.todo.presentation.fragments

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.blackmirrror.todo.data.models.TodoItem

@Parcelize
data class EditTodoItemFragmentArg(val todoItem: TodoItem?) : Parcelable