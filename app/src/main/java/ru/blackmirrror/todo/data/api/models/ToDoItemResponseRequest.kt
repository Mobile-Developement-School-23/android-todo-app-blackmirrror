package ru.blackmirrror.todo.data.api.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import ru.blackmirrror.todo.data.models.Importance
import ru.blackmirrror.todo.data.models.ImportanceAdapter
import ru.blackmirrror.todo.data.models.TodoItem
import java.util.Date

data class ToDoApiResponseList(
    @SerializedName("status")
    val status: String,
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("list")
    val list: List<ToDoItemResponseRequest>
)

data class ToDoApiRequestList(
    @SerializedName("status")
    val status: String,
    @SerializedName("list")
    val list: List<ToDoItemResponseRequest>
)

data class ToDoApiResponseElement(
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("element")
    val element: ToDoItemResponseRequest
)

data class ToDoApiRequestElement(
    @SerializedName("element")
    val element: ToDoItemResponseRequest
)

data class ToDoItemResponseRequest(
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
    val created_at: Long,

    @SerializedName("changed_at")
    val changed_at: Long,

    @SerializedName("last_updated_by")
    val last_updated_by: String,

    @SerializedName("text")
    val text: String
) {
    fun toToDoItem(): TodoItem = TodoItem(
        id,
        text,
        importance,
        deadline?.let { Date(it) },
        done,
        Date(created_at),
        Date(changed_at)
    )
    companion object {
        fun fromToDoTask(toDoItem: TodoItem, deviseId: String): ToDoItemResponseRequest {
            return ToDoItemResponseRequest(
                id = toDoItem.id,
                text = toDoItem.text,
                importance = toDoItem.importance,
                deadline = toDoItem.deadlineDate?.time,
                done = toDoItem.isDone,
                created_at = toDoItem.createdDate.time,
                changed_at = toDoItem.changedDate?.time ?: 0,
                last_updated_by = deviseId,
                color = null
            )
        }
    }
}