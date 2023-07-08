package ru.blackmirrror.todo.data.api.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import ru.blackmirrror.todo.data.models.Importance
import ru.blackmirrror.todo.data.models.ImportanceAdapter
import ru.blackmirrror.todo.data.models.TodoItem
import java.util.Date

/**
 * Responses and requests for api
 */

data class TodoResponseList(
    @SerializedName("status")
    val status: String,
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("list")
    val list: List<TodoItemApi>
)

data class TodoRequestList(
    @SerializedName("status")
    val status: String,
    @SerializedName("list")
    val list: List<TodoItemApi>
)

data class TodoResponseElement(
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("element")
    val element: TodoItemApi
)

data class TodoRequestElement(
    @SerializedName("element")
    val element: TodoItemApi
)

data class TodoItemApi(
    @SerializedName("id")
    val id: String,

    @SerializedName("deadline")
    val deadline: Long?,

    @SerializedName("done")
    val done: Boolean,

    @SerializedName("color")
    val color: String?,

    @SerializedName("importance")
    @JsonAdapter(ImportanceAdapter::class)
    val importance: Importance,

    @SerializedName("created_at")
    val createdAt: Long,

    @SerializedName("changed_at")
    val changedAt: Long,

    @SerializedName("last_updated_by")
    val lastUpdatedBy: String,

    @SerializedName("text")
    val text: String
) {
    fun fromApiToTodoItem(): TodoItem = TodoItem(
        id,
        text,
        importance,
        deadline?.let { Date(it) },
        done,
        Date(createdAt),
        Date(changedAt)
    )
    companion object {
        fun fromTodoItemToApi(toDoItem: TodoItem, deviseId: String): TodoItemApi {
            return TodoItemApi(
                id = toDoItem.id,
                text = toDoItem.text,
                importance = toDoItem.importance,
                deadline = toDoItem.deadlineDate?.time,
                done = toDoItem.isDone,
                createdAt = toDoItem.createdDate.time,
                changedAt = toDoItem.changedDate?.time ?: 0,
                lastUpdatedBy = deviseId,
                color = null
            )
        }
    }
}
