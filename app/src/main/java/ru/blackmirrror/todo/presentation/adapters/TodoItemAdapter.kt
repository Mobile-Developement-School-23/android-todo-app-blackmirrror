package ru.blackmirrror.todo.presentation.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.models.Importance
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.presentation.utils.Utils.formatDate


class TodoItemAdapter(
                      private val listener: RecyclerViewItemClickListener
): RecyclerView.Adapter<TodoItemAdapter.TodoItemViewHolder>() {

    private var todoItems: List<TodoItem> = listOf()

    fun setList(todoItems: List<TodoItem>) {
        this.todoItems = todoItems
        notifyDataSetChanged()
    }

    fun getList(): List<TodoItem> {
        return todoItems
    }

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
            listener.onItemClicked(id, todoItems[position])
        }

        holder.isDone.setOnCheckedChangeListener { _, isChecked ->
            listener.onCheckboxClicked(isChecked, todoItems[position])
        }
    }

    fun getItem(position: Int): TodoItem {
        return todoItems[position]
    }

    inner class TodoItemViewHolder(itemView: View):  ViewHolder(itemView){
        val item = itemView
        val isDone: CheckBox = itemView.findViewById(R.id.item_done)
        val text: TextView = itemView.findViewById(R.id.item_text)
        val info: ImageButton = itemView.findViewById(R.id.item_info_btn)
        val deadline: TextView = itemView.findViewById(R.id.item_deadline)
        val importance: ImageView = itemView.findViewById(R.id.iv_importance)

        fun onBind(todoItem: TodoItem) {
            isDone.isChecked = todoItem.isDone
            text.text = todoItem.text
            if (todoItem.isDone) {
                text.setTextColor(ContextCompat.getColor(item.context, R.color.label_secondary))
                text.paintFlags = text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                importance.setImageResource(0)
                isDone.buttonTintList = AppCompatResources.getColorStateList(
                    itemView.context,
                    R.color.color_green
                )
            }
            else {
                text.setTextColor(ContextCompat.getColor(item.context, R.color.label_primary))
                text.paintFlags = 0
                isDone.buttonTintList = AppCompatResources.getColorStateList(
                    itemView.context,
                    R.color.support_separator
                )
                if (todoItem.importance == Importance.LOW)
                    importance.setImageResource(R.drawable.ic_importance_low)
                else if (todoItem.importance == Importance.IMPORTANT) {
                    importance.setImageResource(R.drawable.ic_importance_high)
                    isDone.buttonTintList = AppCompatResources.getColorStateList(
                        itemView.context,
                        R.color.color_red
                    )
                }
            }
            if (todoItem.deadlineDate != null) {
                deadline.visibility = View.VISIBLE
                deadline.text = formatDate(todoItem.deadlineDate)
            }
            else
                deadline.visibility = View.GONE
        }
    }

    interface RecyclerViewItemClickListener {
        fun onCheckboxClicked(isChecked: Boolean, todoItem: TodoItem)
        fun onItemClicked(id: String, todoItem: TodoItem)
    }

}