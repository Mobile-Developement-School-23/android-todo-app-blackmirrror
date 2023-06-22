package ru.blackmirrror.todo.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.TodoItem
import ru.blackmirrror.todo.data.TodoItemRepository
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.properties.Delegates


class EditTodoItemFragment : Fragment() {

    private lateinit var view: View

    private lateinit var toolbar: Toolbar
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private lateinit var textOfTodo: EditText
    private lateinit var changeImportance: LinearLayout
    private lateinit var importance: TextView
    private lateinit var deadlineSwitcher: Switch
    private lateinit var deadline: TextView

    private lateinit var repository: TodoItemRepository
    private lateinit var positionBundle: String
    var onDataUpdatedListener: OnDataUpdatedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        positionBundle = arguments?.getString("id", "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_edit_todo_item, container, false)

        repository = TodoItemRepository.getInstance()
        initEditFields()
        initToolbar()

        return view
    }

    private fun initToolbar() {
        toolbar = view.findViewById(R.id.toolbar_edit)
        toolbar.title = ""
        saveButton = view.findViewById(R.id.edit_save_btn)
        toolbar.setNavigationIcon(R.drawable.baseline_close_24)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        saveButton.setOnClickListener {
            saveItem()
        }

        deleteButton = view.findViewById(R.id.edit_delete_btn)
        deleteButton.isEnabled = false

        if (positionBundle != "")
            fillFields()
    }

    private fun fillFields() {
        val todoItem: TodoItem? = repository.getItem(positionBundle)
        if (todoItem != null) {
            textOfTodo.setText(todoItem.text)
            importance.text = todoItem.importance
            deadline.text = todoItem.deadline
        }
        deleteButton.isEnabled = true
        deleteButton.setOnClickListener {
            repository.removeItem(positionBundle)
            onDataUpdatedListener?.onDataRemove(positionBundle)
            requireActivity().onBackPressed()
        }
    }

    private fun initEditFields() {
        textOfTodo = view.findViewById(R.id.edit_text)

        changeImportance = view.findViewById(R.id.edit_change_importance)
        importance = view.findViewById(R.id.edit_importance)
        changeImportance.setOnClickListener {
            showPopUpMenu()
        }

        deadlineSwitcher = view.findViewById(R.id.edit_switch_deadline)
        deadline = view.findViewById(R.id.edit_deadline)
        deadlineSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                showDatePickerDialog()
            else
                deadline.text = ""
        }
    }

    private fun showPopUpMenu() {
        val popupMenu = PopupMenu(requireContext(), changeImportance)
        popupMenu.inflate(R.menu.importance)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_default -> {
                    importance.text = "Нет"
                    true
                }
                R.id.action_lower -> {
                    importance.text = "Низкий"
                    true
                }
                R.id.action_default -> {
                    importance.text = "!!Высокий"
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), null,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Готово") { _, _ ->
            val year = datePickerDialog.datePicker.year
            val month = datePickerDialog.datePicker.month
            val dayOfMonth = datePickerDialog.datePicker.dayOfMonth
            deadline.text = getDateFormatted(dayOfMonth, month, year)
        }

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена") { _, _ ->
            deadlineSwitcher.isChecked = false
        }
        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
    }

    private fun saveItem() {
        if (textOfTodo.text.toString() == "") {
            Toast.makeText(requireContext(), "Пожалуйста, напишите текст дела", Toast.LENGTH_SHORT).show()
            return
        }
        val todoItem = (if (positionBundle == "")
            false
        else
            repository.getItem(positionBundle)?.isDone)?.let {
            (if (positionBundle == "")
                UUID.randomUUID().toString()
            else
                repository.getItem(positionBundle)?.id)?.let { it1 ->
                TodoItem(
                    it1,
                    textOfTodo.text.toString(),
                    importance.text.toString(),
                    deadline.text.toString(),
                    it,
                    Date()
                )
            }
        }
        if (positionBundle == "")
            todoItem?.let { repository.addTodoItem(it) }
        else
            todoItem?.let { repository.updateItem(positionBundle, it) }
        onDataUpdatedListener?.onDataUpdated(positionBundle)
        requireActivity().onBackPressed()
    }

    private fun getDateFormatted(dayOfMonth: Int, month: Int, year: Int): String {
        val months = arrayOf("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря")
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val monthText = months[calendar.get(Calendar.MONTH)]
        return "$dayOfMonth $monthText $year г."
    }

    interface OnDataUpdatedListener {
        fun onDataUpdated(id: String)
        fun onDataRemove(id: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onDataUpdatedListener = context as OnDataUpdatedListener
        } catch (_: ClassCastException) {
        }
    }
}