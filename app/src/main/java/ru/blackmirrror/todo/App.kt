package ru.blackmirrror.todo

import android.app.Application
import android.content.Context
import androidx.room.Room
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.data.api.ApiFactory
import ru.blackmirrror.todo.data.local.TodoItemDb
import ru.blackmirrror.todo.presentation.utils.DataUpdateInitializer
import ru.blackmirrror.todo.presentation.utils.ServiceLocator
import ru.blackmirrror.todo.presentation.utils.locale

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        DataUpdateInitializer(this)
        ServiceLocator.register<Context>(this)

        ServiceLocator.register(
            Room.databaseBuilder(locale(), TodoItemDb::class.java, "todo_items_db")
            .build())
        ServiceLocator.register(ApiFactory.create())
        ServiceLocator.register(SharedPrefs(locale()))
        //ServiceLocator.register(NetworkConnectivityObserver(this))
        ServiceLocator.register(TodoRepository())
    }
}