package ru.blackmirrror.todo.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.blackmirrror.todo.data.models.Importance
import java.util.Date

@Parcelize
data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadlineDate: Date?,
    var isDone: Boolean,
    val createdDate: Date,
    val changedDate: Date? = null
) : Parcelable
