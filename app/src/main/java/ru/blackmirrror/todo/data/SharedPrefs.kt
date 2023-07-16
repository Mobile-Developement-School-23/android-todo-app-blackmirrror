package ru.blackmirrror.todo.data

import android.content.Context
import android.util.Log

/**
 * Device data store
 */

class SharedPrefs(context: Context) {

    companion object {
        private const val REVISION_TAG = "revision"
        private const val NOTIFICATIONS_TAG = "notifications"
        private const val THEME_TAG = "theme"
    }

    private val sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun putRevision(revision: Int) {
        editor.putInt(REVISION_TAG, revision)
        editor.apply()
    }

    fun getRevision() : Int {
        return sharedPreferences.getInt(REVISION_TAG, 0)
    }

    fun putNotifications(value: Boolean) {
        editor.putBoolean(NOTIFICATIONS_TAG, value)
        editor.apply()
    }

    fun getNotifications() : Boolean {
        return sharedPreferences.getBoolean(NOTIFICATIONS_TAG, false)
    }

    fun putTheme(value:Int) {
        editor.putInt(THEME_TAG, value)
        editor.apply()
    }

    fun getTheme() : Int {
        return sharedPreferences.getInt(THEME_TAG, 0)
    }
}
