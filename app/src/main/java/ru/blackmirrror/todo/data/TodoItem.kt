package ru.blackmirrror.todo.data

import java.util.Date

data class TodoItem(
    val id: String,
    val text: String,
    val importance: String,
    val deadline: String? = "",
    var isDone: Boolean,
    val createdDate: Date,
    val changedDate: Date? = null
)
