package ru.blackmirrror.todo.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.data.api.models.ToDoApiRequestElement
import ru.blackmirrror.todo.data.api.models.ToDoApiResponseElement
import ru.blackmirrror.todo.data.api.models.ToDoApiResponseList
import ru.blackmirrror.todo.data.api.models.ToDoItemResponseRequest

class TodoRepository {

    private val apiService = ApiFactory.create()

    object ApiFactory {

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(makeLoggingInterceptor())
            .build()

        private fun makeLoggingInterceptor(): HttpLoggingInterceptor {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            return logging
        }

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun create(): ApiService {
            return retrofit.create(ApiService::class.java)
        }
    }

    companion object {
        private const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"
    }

    suspend fun getRemoteTasks(): NetworkState<ToDoApiResponseList> {
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

    suspend fun getRemoteTask(id: String): NetworkState<ToDoApiResponseElement> {
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

    suspend fun createRemoteOneTask(newTask: TodoItem, revision: Int): NetworkState<ToDoApiResponseElement> {
        val response = apiService.addTask(
            //lastKnownRevision = sharedPreferences.getRevisionId(),
            //ToDoApiRequestElement(ToDoItemResponseRequest.fromToDoTask(newTask, sharedPreferences.getDeviceId()?: "null"))
            lastKnownRevision = revision,
            ToDoApiRequestElement(ToDoItemResponseRequest.fromToDoTask(newTask, "de"))
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

    suspend fun updateRemoteOneTask(toDoTask: TodoItem, revision: Int): NetworkState<ToDoApiResponseElement> {
        val response = apiService.updateTask(
//            lastKnownRevision = sharedPreferences.getRevisionId(),
//            itemId = toDoTask.id,
//            ToDoApiRequestElement(ToDoItemResponseRequest.fromToDoTask(toDoTask, sharedPreferences.getDeviceId()?: "null"))
            lastKnownRevision = revision,
            itemId = toDoTask.id,
            ToDoApiRequestElement(ToDoItemResponseRequest.fromToDoTask(toDoTask, "dev"))
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

    suspend fun deleteRemoteOneTask(toDoTask: TodoItem, revision: Int): NetworkState<ToDoApiResponseElement> {
        val response = apiService.deleteTask(
            //lastKnownRevision = sharedPreferences.getRevisionId(),
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