package ru.blackmirrror.todo.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * User-authorization class
 */

class AuthInterceptor : Interceptor {
    private val authToken = "maestri"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        return chain.proceed(request)
    }
}
