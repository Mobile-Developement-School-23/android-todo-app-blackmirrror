package ru.blackmirrror.todo.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.TodoItem
import ru.blackmirrror.todo.presentation.utils.Utils.formatDate


class TodoItemAdapter(private var todoItems: MutableList<TodoItem>,
                      private val listener: RecyclerViewItemClickListener
): RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todoItems.size
    }

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.onBind(todoItems[position])
        val id = todoItems[position].id

        holder.item.setOnClickListener {
            listener.onItemClicked(id)
        }

        holder.isDone.setOnCheckedChangeListener { _, isChecked ->
            listener.onCheckboxClicked(id, isChecked)
        }

        holder.info.setOnClickListener {
            //listener.onImageButtonClicked(id)
        }
    }

    fun getItem(id: String): TodoItem? {
        return todoItems.find { it.id == id }
    }

    fun addNewItem(todoItem: TodoItem) {
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

    inner class TodoItemViewHolder(itemView: View):  ViewHolder(itemView){
        val item = itemView
        val isDone: CheckBox = itemView.findViewById(R.id.item_done)
        val text: TextView = itemView.findViewById(R.id.item_text)
        val info: ImageButton = itemView.findViewById(R.id.item_info_btn)
        val deadline: TextView = itemView.findViewById(R.id.item_deadline)

        fun onBind(todoItem: TodoItem) {
            isDone.isChecked = todoItem.isDone
            text.text = todoItem.text
            if (todoItem.deadlineDate != null) {
                deadline.visibility = View.VISIBLE
                deadline.text = formatDate(todoItem.deadlineDate)
            }
            else
                deadline.visibility = View.GONE
        }
    }

    interface RecyclerViewItemClickListener {
        fun onCheckboxClicked(id: String, isChecked: Boolean)
        fun onItemClicked(id: String)
        //fun onImageButtonClicked(id: String)
    }

}