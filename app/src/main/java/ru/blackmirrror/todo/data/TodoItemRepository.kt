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
        TodoItem(UUID.randomUUID().toString(),"Купить продукты на неделю", Importance.LOW, Date(), false, Date()),
        TodoItem(UUID.randomUUID().toString(), "Заказать билеты в кино", Importance.DEFAULT, Date(), true, Date()),
        TodoItem(UUID.randomUUID().toString(), "Сделать домашнюю работу", Importance.HIGH, Date(), false, Date()),
        TodoItem(UUID.randomUUID().toString(), "Что-то ооочень длиииииииииинноеееееееее ооооооооооооооочень сииииииииииииииииииииииильно днииииииииииииииииииииинное", Importance.HIGH, Date(), false, Date()),
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