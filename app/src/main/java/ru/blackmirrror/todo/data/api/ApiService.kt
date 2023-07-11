package ru.blackmirrror.todo.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.blackmirrror.todo.data.api.models.TodoRequestElement
import ru.blackmirrror.todo.data.api.models.TodoRequestList
import ru.blackmirrror.todo.data.api.models.TodoResponseElement
import ru.blackmirrror.todo.data.api.models.TodoResponseList

/**
 * Service of requests to server
 */

interface ApiService {
    @GET("list")
    suspend fun getList(): Response<TodoResponseList>

    @PATCH("list")
    suspend fun updateList(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body body: TodoRequestList
    ): Response<TodoResponseList>

    @GET("list/{id}")
    suspend fun getTaskById(@Path("id") itemId: String): Response<TodoResponseElement>

    @POST("list")
    suspend fun addTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body newItem: TodoRequestElement
    ): Response<TodoResponseElement>

    @PUT("list/{id}")
    suspend fun updateTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Path("id") itemId: String,
        @Body body: TodoRequestElement
    ): Response<TodoResponseElement>

    @DELETE("list/{id}")
    suspend fun deleteTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Path("id") itemId: String,
    ): Response<TodoResponseElement>
}
