package ru.blackmirrror.todo.data

import android.content.Context
import ru.blackmirrror.todo.data.api.ApiService
import ru.blackmirrror.todo.data.api.NetworkState
import ru.blackmirrror.todo.data.api.NetworkUtils
import ru.blackmirrror.todo.data.api.models.TodoItemApi
import ru.blackmirrror.todo.data.api.models.TodoRequestElement
import ru.blackmirrror.todo.data.api.models.TodoRequestList
import ru.blackmirrror.todo.data.api.models.TodoResponseElement
import ru.blackmirrror.todo.data.api.models.TodoResponseList
import ru.blackmirrror.todo.data.local.TodoItemDb
import ru.blackmirrror.todo.data.local.TodoItemEntity
import ru.blackmirrror.todo.data.local.TodoOperationEntity
import ru.blackmirrror.todo.data.local.TodoOperationEntity.Companion.TAG_CREATE
import ru.blackmirrror.todo.data.local.TodoOperationEntity.Companion.TAG_DELETE
import ru.blackmirrror.todo.data.local.TodoOperationEntity.Companion.TAG_UPDATE
import ru.blackmirrror.todo.data.models.TodoItem
import java.util.UUID
import javax.inject.Inject

/**
 * Repository between ViewModel and apiService relies CRUD-operations
 */

class TodoRepository @Inject constructor(
    val context: Context, database: TodoItemDb, private val apiService: ApiService
) {

    private val sharedPrefs = SharedPrefs(context)
    private val todoItemDao = database.todoItemDao()
    private val todoOperationDao = database.todoOperationDao()

    fun getAllTodoItemsNoFlow(): List<TodoItem> {
        return todoItemDao.getTodoItemsNoFlow().map { it.fromEntityToTodoItem() }
    }

    suspend fun fetchTodoList() {
        if (NetworkUtils.isInternetConnected(context)) {
            when (val response = getRemoteTasks()) {
                is NetworkState.Success -> {
                    sharedPrefs.putRevision(response.data.revision)
                    mergeList(response.data.list.map { it.fromApiToTodoItem() })
                }

                is NetworkState.Error -> println("Internet error ${response.response.code()}")
            }
            updateRemoteTasks(getAllTodoItemsNoFlow())
        }
    }

    suspend fun mergeList(serverList: List<TodoItem>) {
        cleanLocalList()
        for (serverItem in serverList) {
            val localItem = todoItemDao.getTodoItemById(serverItem.id)
            if (localItem != null) {
                if (serverItem.changedDate!! > localItem.fromEntityToTodoItem().changedDate) {
                    todoItemDao.updateTodoItem(TodoItemEntity.fromTodoItemToEntity(serverItem))
                    todoOperationDao.deleteOperationsByItemId(serverItem.id) }
            } else {
                val operations = todoOperationDao.getTodoOperationsByItemId(serverItem.id)
                val flag = operations?.any { it.additionalField == TAG_DELETE } ?: false
                if (!flag) {
                    todoItemDao.createTodoItem(TodoItemEntity.fromTodoItemToEntity(serverItem))
                }
            }
        }
        cleanLocalListAfterLoading(serverList)
    }

    private suspend fun cleanLocalList() {
        for (localItem in getAllTodoItemsNoFlow()) {
            val ops = todoOperationDao.getTodoOperationsByItemId(localItem.id)
            if (ops == null)
                todoItemDao.deleteTodoItem(TodoItemEntity.fromTodoItemToEntity(localItem))
            else if (ops.find { it.additionalField != TAG_DELETE } == null)
                todoItemDao.deleteTodoItem(TodoItemEntity.fromTodoItemToEntity(localItem))
        }
    }
    private suspend fun cleanLocalListAfterLoading(serverList: List<TodoItem>) {
        for (o in todoOperationDao.getTodoOperationsNoFlow()) {
            if (o.additionalField == TAG_UPDATE && serverList.find { it.id == o.todoItemId } == null)
                todoItemDao.getTodoItemById(o.todoItemId)?.let { todoItemDao.deleteTodoItem(it) }
        }
        todoOperationDao.deleteAllOperations()
    }

    suspend fun createTask(todoItem: TodoItem) {
        if (NetworkUtils.isInternetConnected(context))
            TodoRepositoryRequest().createRemoteOneTask(todoItem, sharedPrefs.getRevision())
        else {
            TodoRepositoryRequest().createOperation(
                TodoOperationEntity(UUID.randomUUID().toString(), todoItem.id, TAG_CREATE)
            )
        }
        return todoItemDao.createTodoItem(TodoItemEntity.fromTodoItemToEntity(todoItem))
    }

    suspend fun updateTask(newItem: TodoItem) {
        if (NetworkUtils.isInternetConnected(context))
            TodoRepositoryRequest().updateRemoteOneTask(newItem, sharedPrefs.getRevision())
        else {
            TodoRepositoryRequest().createOperation(
                TodoOperationEntity(UUID.randomUUID().toString(), newItem.id, TAG_UPDATE)
            )
        }
        todoItemDao.updateTodoItem(TodoItemEntity.fromTodoItemToEntity(newItem))
    }

    suspend fun deleteTask(todoItem: TodoItem) {
        if (NetworkUtils.isInternetConnected(context))
            TodoRepositoryRequest().deleteRemoteOneTask(todoItem, sharedPrefs.getRevision())
        else {
            TodoRepositoryRequest().createOperation(
                TodoOperationEntity(UUID.randomUUID().toString(), todoItem.id, TAG_DELETE)
            )
        }
        return todoItemDao.deleteTodoItem(TodoItemEntity.fromTodoItemToEntity(todoItem))
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
                NetworkState.Success(responseBody)
            } else
                NetworkState.Error(response)
        } else
            NetworkState.Error(response)
    }
    suspend fun getRemoteTasks(): NetworkState<TodoResponseList> {
        val response = apiService.getList()
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                sharedPrefs.putRevision(responseBody.revision)
                NetworkState.Success(responseBody)
            } else
                NetworkState.Error(response)
        } else
            NetworkState.Error(response)
    }
    inner class TodoRepositoryRequest {
        suspend fun createRemoteOneTask(newTask: TodoItem, revision: Int)
                : NetworkState<TodoResponseElement> {
            val response = apiService.addTask(
                lastKnownRevision = revision,
                TodoRequestElement(TodoItemApi.fromTodoItemToApi(newTask, "de"))
            )
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    sharedPrefs.putRevision(responseBody.revision)
                    NetworkState.Success(responseBody)
                } else
                    NetworkState.Error(response)
            } else
                NetworkState.Error(response)
        }
        suspend fun updateRemoteOneTask(toDoTask: TodoItem, revision: Int)
                : NetworkState<TodoResponseElement> {
            val response = apiService.updateTask(
                lastKnownRevision = revision,
                itemId = toDoTask.id,
                TodoRequestElement(TodoItemApi.fromTodoItemToApi(toDoTask, "de"))
            )
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    sharedPrefs.putRevision(responseBody.revision)
                    NetworkState.Success(responseBody)
                } else
                    NetworkState.Error(response)
            } else
                NetworkState.Error(response)
        }
        suspend fun deleteRemoteOneTask(toDoTask: TodoItem, revision: Int)
                : NetworkState<TodoResponseElement> {
            val response = apiService.deleteTask(revision, toDoTask.id)
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    sharedPrefs.putRevision(responseBody.revision)
                    NetworkState.Success(responseBody)
                } else
                    NetworkState.Error(response)
            } else
                NetworkState.Error(response)
        }
        suspend fun createOperation(operationEntity: TodoOperationEntity) {
            return todoOperationDao.createTodoOperation(operationEntity)
        }
    }
}
