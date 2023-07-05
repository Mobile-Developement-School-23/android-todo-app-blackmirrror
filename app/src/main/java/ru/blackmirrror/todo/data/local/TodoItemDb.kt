package ru.blackmirrror.todo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TodoItemEntity::class, TodoOperationEntity::class], version = 1)
abstract class TodoItemDb : RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
    abstract fun todoOperationDao(): TodoOperationDao
}