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
import ru.blackmirrror.todo.data.api.models.ToDoApiRequestElement
import ru.blackmirrror.todo.data.api.models.ToDoApiRequestList
import ru.blackmirrror.todo.data.api.models.ToDoApiResponseElement
import ru.blackmirrror.todo.data.api.models.ToDoApiResponseList


interface ApiService {
    @GET("list")
    suspend fun getList(): Response<ToDoApiResponseList>

    @PATCH("list")
    suspend fun updateList(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body body: ToDoApiRequestList
    ): Response<ToDoApiResponseList>

    @GET("list/{id}")
    suspend fun getTaskById(@Path("id") itemId: String): Response<ToDoApiResponseElement>

    @POST("list")
    suspend fun addTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body newItem: ToDoApiRequestElement
    ): Response<ToDoApiResponseElement>

    @PUT("list/{id}")
    suspend fun updateTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Path("id") itemId: String,
        @Body body: ToDoApiRequestElement
    ): Response<ToDoApiResponseElement>

    @DELETE("list/{id}")
    suspend fun deleteTask(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Path("id") itemId: String,
    ): Response<ToDoApiResponseElement>
}