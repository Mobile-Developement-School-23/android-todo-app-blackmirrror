package ru.blackmirrror.todo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Local database CRUD-operations with items table
 */

@Dao
interface TodoItemDao {

    @Query("SELECT * FROM todo_items ORDER BY deadlineDate")
    fun getTodoItems(): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM todo_items ORDER BY deadlineDate")
    fun getTodoItemsNoFlow(): List<TodoItemEntity>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    fun getTodoItemById(id: String): TodoItemEntity?

    @Update
    suspend fun updateTodoItem(toDoItemEntity: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTodoItem(vararg itemEntity: TodoItemEntity)

    @Query("DELETE FROM todo_items")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteTodoItem(toDoItemEntity: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun mergeTodoItems(todoItems: List<TodoItemEntity>)
}
