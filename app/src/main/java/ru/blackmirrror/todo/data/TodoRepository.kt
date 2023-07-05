package ru.blackmirrror.todo.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import ru.blackmirrror.todo.data.api.ApiFactory
import ru.blackmirrror.todo.data.api.NetworkState
import ru.blackmirrror.todo.data.api.models.TodoRequestElement
import ru.blackmirrror.todo.data.api.models.TodoResponseElement
import ru.blackmirrror.todo.data.api.models.TodoResponseList
import ru.blackmirrror.todo.data.api.models.TodoItemApi
import ru.blackmirrror.todo.data.api.models.TodoRequestList
import ru.blackmirrror.todo.data.local.TodoItemDb
import ru.blackmirrror.todo.data.local.TodoItemEntity
import ru.blackmirrror.todo.data.local.TodoOperationEntity
import ru.blackmirrror.todo.data.local.TodoOperationEntity.Companion.TAG_CREATE
import ru.blackmirrror.todo.data.local.TodoOperationEntity.Companion.TAG_DELETE
import ru.blackmirrror.todo.data.models.TodoItem

class TodoRepository(localDataSource: TodoItemDb, private val sharedPrefs: SharedPrefs) {

    private val apiService = ApiFactory.create()
    private val todoItemDao = localDataSource.todoItemDao()
    private val todoOperationDao = localDataSource.todoOperationDao()

    fun getAllTodoItems(): Flow<List<TodoItem>> {
        return todoItemDao.getTodoItems().map { it -> it.map { it.fromEntityToTodoItem() } }
    }

    fun getAllTodoItemsNoFlow(): List<TodoItem> {
        return todoItemDao.getTodoItemsNoFlow().map { it.fromEntityToTodoItem() }
    }

    fun getTodoItemById(id: String): TodoItem? {
        val item = todoItemDao.getTodoItemById(id = id)
        if (item != null)
            return todoItemDao.getTodoItemById(id = id).fromEntityToTodoItem()
        return null
    }

    suspend fun updateTodoItem(toDoItem: TodoItem) {
        val toDoItemEntity = TodoItemEntity.fromTodoItemToEntity(toDoItem)
        return todoItemDao.updateTodoItem(toDoItemEntity)
    }

    suspend fun createTodoItem(toDoItem: TodoItem) {
        val todoItemEntity = TodoItemEntity.fromTodoItemToEntity(toDoItem)
        return todoItemDao.createTodoItem(todoItemEntity)
    }

    suspend fun deleteTodoItem(toDoItem: TodoItem) {
        val todoItemEntity = TodoItemEntity.fromTodoItemToEntity(toDoItem)
        return todoItemDao.deleteTodoItem(todoItemEntity)
    }

    suspend fun mergeList(serverList: List<TodoItem>) {
        for (localItem in getAllTodoItemsNoFlow()) {
            val ops = todoOperationDao.getTodoOperationsByItemId(localItem.id)
            if (ops == null) {
                Log.d("API", "mergeList: deleted not changed $localItem")
                todoItemDao.deleteTodoItem(TodoItemEntity.fromTodoItemToEntity(localItem))
            }
            else if (ops.find { it.additionalField == TAG_CREATE } == null) {
                Log.d("API", "mergeList: deleted not created $localItem")
                todoItemDao.deleteTodoItem(TodoItemEntity.fromTodoItemToEntity(localItem))
            }
        }
        Log.d("API", "mergeList: list ${todoItemDao.getTodoItemsNoFlow()}")
        for (serverItem in serverList) {
            val localItem = todoItemDao.getTodoItemById(serverItem.id)
            if (localItem != null) {
                if (serverItem.changedDate!! > localItem.fromEntityToTodoItem().changedDate) {
                    todoItemDao.updateTodoItem(TodoItemEntity.fromTodoItemToEntity(serverItem))
                    todoOperationDao.deleteOperationsByItemId(serverItem.id)
                }
            }
            else {
                val operations = todoOperationDao.getTodoOperationsByItemId(serverItem.id)
                var flag = false
                if (operations != null) {
                    for (o in operations) {
                        if (o.additionalField == TAG_DELETE)
                            flag = true
                    }
                }
                if (!flag)
                    todoItemDao.createTodoItem(TodoItemEntity.fromTodoItemToEntity(serverItem))
            }
        }
        //todoItemDao.mergeTodoItems(currentList.map { TodoItemEntity.fromTodoItemToEntity(it) })
    }

    suspend fun updateRemoteTasks(items: List<TodoItem>): NetworkState<TodoResponseList> {
        val response = apiService.updateList(
            lastKnownRevision = sharedPrefs.getRevision(),
            TodoRequestList("ok", items.map { TodoItemApi.fromTodoItemToApi(it, "de") })
        )
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                sharedPrefs.putRevision(responseBody.revision)
                Log.d("API", "updateRemoteTasks: ")
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun getRemoteTasks(): NetworkState<TodoResponseList> {
        val response = apiService.getList()
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun getRemoteTask(id: String): NetworkState<TodoResponseElement> {
        val response = apiService.getTaskById(id)
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun createRemoteOneTask(newTask: TodoItem, revision: Int): NetworkState<TodoResponseElement> {
        val response = apiService.addTask(
            lastKnownRevision = revision,
            TodoRequestElement(TodoItemApi.fromTodoItemToApi(newTask, "de"))
        )

        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                sharedPrefs.putRevision(responseBody.revision)
                Log.d("API", "createRemoteOneTask: ")
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun updateRemoteOneTask(toDoTask: TodoItem, revision: Int): NetworkState<TodoResponseElement> {
        val response = apiService.updateTask(
            lastKnownRevision = revision,
            itemId = toDoTask.id,
            TodoRequestElement(TodoItemApi.fromTodoItemToApi(toDoTask, "de"))
        )

        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                sharedPrefs.putRevision(responseBody.revision)
                Log.d("API", "updateRemoteOneTask: ")
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    suspend fun deleteRemoteOneTask(toDoTask: TodoItem, revision: Int): NetworkState<TodoResponseElement> {
        val response = apiService.deleteTask(
            lastKnownRevision = revision,
            itemId = toDoTask.id
        )

        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                sharedPrefs.putRevision(responseBody.revision)
                Log.d("API", "deleteRemoteOneTask: ")
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }

    fun getAllOperations(): List<TodoOperationEntity> {
        return todoOperationDao.getTodoOperationsNoFlow()
    }

    fun getOperationsByItemId(id: String): List<TodoOperationEntity> {
        return todoOperationDao.getTodoOperationsByItemId(id)
    }

    suspend fun createOperation(operationEntity: TodoOperationEntity) {
        return todoOperationDao.createTodoOperation(operationEntity)
    }

    suspend fun deleteOperation(operationEntity: TodoOperationEntity) {
        todoOperationDao.deleteTodoOperation(operationEntity)
    }

    suspend fun deleteAllOperations() {
        todoOperationDao.deleteAllOperations()
    }
}