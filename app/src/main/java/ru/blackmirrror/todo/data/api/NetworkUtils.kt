package ru.blackmirrror.todo.data.api

import android.content.Context
import android.net.ConnectivityManager

/**
 * Network availability check functionality
 */

object NetworkUtils {
    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
