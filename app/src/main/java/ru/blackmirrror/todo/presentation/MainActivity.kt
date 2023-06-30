package ru.blackmirrror.todo.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.local.TodoItemDb
import ru.blackmirrror.todo.presentation.fragments.TodoItemsViewModel
import ru.blackmirrror.todo.presentation.fragments.ViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TodoItemsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val database = Room.databaseBuilder(applicationContext, TodoItemDb::class.java, "todo_items_db")
//            .build()

        val viewModelFactory = ViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory)[TodoItemsViewModel::class.java]
        setContentView(R.layout.activity_main)
    }
}