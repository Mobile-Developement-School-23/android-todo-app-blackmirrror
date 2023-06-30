package ru.blackmirrror.todo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {

    @Query("SELECT * FROM todo_items")
    fun getTodoItems(): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM todo_items")
    fun getTodoItemsNoFlow(): List<TodoItemEntity>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    fun getTodoItemById(id: String): TodoItemEntity

    @Update
    suspend fun updateTodoItem(toDoItemEntity: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTodoItem(vararg itemEntity: TodoItemEntity)

    @Delete
    suspend fun deleteTodoItem(toDoItemEntity: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun initTodoItems(todoItems: List<TodoItemEntity>)
}