package ru.blackmirrror.todo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Local database CRUD-operations with operations table
 */

@Dao
interface TodoOperationDao {
    @Query("SELECT * FROM todo_operations")
    fun getTodoOperations(): Flow<List<TodoOperationEntity>>

    @Query("SELECT * FROM todo_operations")
    fun getTodoOperationsNoFlow(): List<TodoOperationEntity>

    @Query("SELECT * FROM todo_operations WHERE todoItemId = :id")
    fun getTodoOperationsByItemId(id: String): List<TodoOperationEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTodoOperation(vararg operationEntity: TodoOperationEntity)

    @Query("DELETE FROM todo_operations")
    suspend fun deleteAllOperations()

    @Query("DELETE FROM todo_operations WHERE todoItemId = :itemId")
    suspend fun deleteOperationsByItemId(itemId: String)

    @Delete
    suspend fun deleteTodoOperation(operationEntity: TodoOperationEntity)
}
