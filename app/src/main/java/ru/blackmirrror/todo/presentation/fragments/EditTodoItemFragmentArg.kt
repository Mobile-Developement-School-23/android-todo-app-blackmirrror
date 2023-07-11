package ru.blackmirrror.todo.presentation.fragments

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.blackmirrror.todo.data.models.TodoItem

/**
 * Parsing args between fragments
 */

@Parcelize
data class EditTodoItemFragmentArg(val todoItem: TodoItem?) : Parcelable
