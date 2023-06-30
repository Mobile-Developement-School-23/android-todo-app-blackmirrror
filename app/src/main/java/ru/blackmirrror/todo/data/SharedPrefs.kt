package ru.blackmirrror.todo.data

import android.content.Context

class SharedPrefs(context: Context) {

    companion object {
        private const val REVISION_TAG = "revision"
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
}