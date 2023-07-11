package ru.blackmirrror.todo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.blackmirrror.todo.data.models.Importance
import ru.blackmirrror.todo.data.models.TodoItem
import java.util.Date

/**
 * Entities for local database in items table
 */

@Entity(tableName = "todo_items")
data class TodoItemEntity(
    @PrimaryKey @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "importance") var importance: Importance,
    @ColumnInfo(name = "deadlineDate") var deadlineDate: Long?,
    @ColumnInfo(name = "done") var done: Boolean,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "changedAt") var changedAt: Long?
) {
    fun fromEntityToTodoItem(): TodoItem = TodoItem(
        id,
        text,
        importance,
        deadlineDate?.let { Date(it) },
        done,
        Date(createdAt),
        changedAt?.let { Date(it) }
    )
    companion object {
        fun fromTodoItemToEntity(toDoItem: TodoItem): TodoItemEntity {
            return TodoItemEntity(
                id = toDoItem.id,
                text = toDoItem.text,
                importance = toDoItem.importance,
                deadlineDate  = toDoItem.deadlineDate?.time,
                done = toDoItem.isDone,
                createdAt = toDoItem.createdDate.time,
                changedAt = toDoItem.changedDate?.time
            )
        }
    }
}
