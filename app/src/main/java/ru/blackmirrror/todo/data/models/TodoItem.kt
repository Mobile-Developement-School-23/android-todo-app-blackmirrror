package ru.blackmirrror.todo.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.blackmirrror.todo.data.models.Importance
import java.util.Date

/**
 * Base class TodoItem
 */

@Parcelize
data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadlineDate: Date?,
    val isDone: Boolean,
    val createdDate: Date,
    val changedDate: Date? = null
) : Parcelable
