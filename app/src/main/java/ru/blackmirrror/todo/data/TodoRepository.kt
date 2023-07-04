package ru.blackmirrror.todo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.blackmirrror.todo.data.api.ApiFactory
import ru.blackmirrror.todo.data.api.NetworkState
import ru.blackmirrror.todo.data.api.models.TodoRequestElement
import ru.blackmirrror.todo.data.api.models.TodoResponseElement
import ru.blackmirrror.todo.data.api.models.TodoResponseList
import ru.blackmirrror.todo.data.api.models.TodoItemApi
import ru.blackmirrror.todo.data.local.TodoItemDb
import ru.blackmirrror.todo.data.local.TodoItemEntity
import ru.blackmirrror.todo.data.models.TodoItem

class TodoRepository(localDataSource: TodoItemDb) {

    private val apiService = ApiFactory.create()
    private val todoItemDao = localDataSource.todoItemDao()

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

    suspend fun initList(currentList: List<TodoItem>, revision: Int) {
//        for (item in currentList) {
//            val localItem = getTodoItemById(item.id)
//            if (localItem != null) {
//                if (localItem.changedDate!= null) {
//                    if (localItem.changedDate < item.changedDate) {
//                        updateTodoItem(item)
//                    }
//                    //else
//                        //updateRemoteOneTask(item, revision)
//                }
//            } else {
//                createTodoItem(item)
//            }
//        }
        todoItemDao.initTodoItems(currentList.map { TodoItemEntity.fromTodoItemToEntity(it) })
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
                //sharedPreferences.putRevisionId(responseBody.revision)
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
            TodoRequestElement(TodoItemApi.fromTodoItemToApi(toDoTask, "dev"))
        )

        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                //sharedPreferences.putRevisionId(responseBody.revision)
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
                //sharedPreferences.putRevisionId(responseBody.revision)
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }
}