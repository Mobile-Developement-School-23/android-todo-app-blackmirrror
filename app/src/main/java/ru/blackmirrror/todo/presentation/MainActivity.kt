package ru.blackmirrror.todo.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.presentation.fragments.TodoItemsViewModel


class MainActivity : AppCompatActivity() {

    private val viewModel: TodoItemsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}