package ru.blackmirrror.todo.data

import java.util.Date
import java.util.UUID

class TodoItemRepository private constructor() {

    companion object {
        @Volatile
        private var instance: TodoItemRepository? = null

        fun getInstance(): TodoItemRepository {
            return instance ?: synchronized(this) {
                instance ?: TodoItemRepository().also { instance = it }
            }
        }
    }

    private var todoItems = mutableListOf(
        TodoItem(UUID.randomUUID().toString(),"Купить продукты на неделю", "Нет", "22 июля 2022 г.", false, Date()),
        TodoItem(UUID.randomUUID().toString(), "Заказать билеты в кино", "Низкая", "22 июля 2022 г.", true, Date()),
        TodoItem(UUID.randomUUID().toString(), "Сделать домашнюю работу", "Высокая", "22 июля 2022 г.", false, Date()),
        TodoItem(UUID.randomUUID().toString(), "Что-то ооочень длиииииииииинноеееееееее ооооооооооооооочень сииииииииииииииииииииииильно днииииииииииииииииииииинное", "Нет", "22 июля 2022 г.", false, Date()),
//        TodoItem(UUID.randomUUID().toString(), "Посетить родственников", "Высокая", "22 июля 2022 г.", false, Date()),
//        TodoItem(UUID.randomUUID().toString(), "Получить водительские права", "Низкая", "22 июля 2022 г.", true, Date()),
//        TodoItem(UUID.randomUUID().toString(), "Купить продукты на неделю", "Нет", "22 июля 2022 г.", false, Date()),
//        TodoItem(UUID.randomUUID().toString(), "Заказать билеты в кино", "Низкая", "22 июля 2022 г.", true, Date()),
//        TodoItem(UUID.randomUUID().toString(), "Сделать домашнюю работу", "Высокая", "22 июля 2022 г.", false, Date()),
//        TodoItem(UUID.randomUUID().toString(), "Разобраться с налогами", "Высокая", "22 июля 2022 г.", false, Date()),
//        TodoItem(UUID.randomUUID().toString(), "Посетить родственников", "Нет", "22 июля 2022 г.", false, Date()),
//        TodoItem(UUID.randomUUID().toString(), "Получить водительские права", "Низкая", "22 июля 2022 г.", true, Date()),
    )

    fun getAllTodoItems(): MutableList<TodoItem> {
        return todoItems
    }

    fun getDoneTodoItems(): MutableList<TodoItem> {
        val res = mutableListOf<TodoItem>()
        todoItems.forEach {
            if (it.isDone)
                res.add(it)
        }
        return res
    }

    fun addTodoItem(todoItem: TodoItem) {
        todoItems.add(todoItem)
    }

    fun updateItem(id: String, todoItem: TodoItem): Boolean {
        val index = todoItems.indexOfFirst { it.id == id }
        if (index == -1) return false
        todoItems[index] = todoItem
        return true
    }

    fun removeItem(id: String): Boolean {
        val index = todoItems.indexOfFirst { it.id == id }
        if (index == -1) return false
        todoItems.removeAt(index)
        return true
    }

    fun getItem(id: String): TodoItem? {
        return todoItems.find { it.id == id }
    }
}