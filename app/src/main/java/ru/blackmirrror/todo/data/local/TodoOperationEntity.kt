package ru.blackmirrror.todo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_operations")
data class TodoOperationEntity(
    @PrimaryKey @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "todoItemId") var todoItemId: String,
    @ColumnInfo(name = "additionalField") var additionalField: String
) {
    companion object {
        const val TAG_CREATE = "create"
        const val TAG_UPDATE = "update"
        const val TAG_DELETE = "delete"
    }
}