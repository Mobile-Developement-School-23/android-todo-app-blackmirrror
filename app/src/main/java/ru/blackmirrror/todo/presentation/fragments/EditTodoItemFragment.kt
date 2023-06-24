package ru.blackmirrror.todo.presentation.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.Importance
import ru.blackmirrror.todo.data.TodoItem
import ru.blackmirrror.todo.data.TodoItemRepository
import ru.blackmirrror.todo.databinding.FragmentEditTodoItemBinding
import ru.blackmirrror.todo.presentation.utils.Utils.formatDate
import java.util.Calendar
import java.util.Date
import java.util.UUID


class EditTodoItemFragment : Fragment() {

    private lateinit var binding: FragmentEditTodoItemBinding

    private lateinit var repository: TodoItemRepository
    var onDataUpdatedListener: OnDataUpdatedListener? = null

    private var saveImportance: Importance = Importance.DEFAULT
    private var saveDeadlineDate: Date? = null
    private lateinit var currentId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTodoItemBinding.inflate(inflater, container, false)

        repository = TodoItemRepository.getInstance()
        initEditFields()
        initToolbar()

        return binding.root
    }

    private fun initToolbar() {
        binding.toolbarEdit.title = ""
        binding.toolbarEdit.setNavigationIcon(R.drawable.baseline_close_24)
        binding.toolbarEdit.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.editSaveBtn.setOnClickListener {
            saveItem()
        }

        currentId = arguments?.getString("todoItemId", "").toString()
        if (currentId != "")
            fillFields()
    }

    private fun fillFields() {
        val currentItem: TodoItem? = repository.getItem(currentId)
        if (currentItem != null) {
            binding.editText.setText(currentItem.text)
            saveImportance = currentItem.importance
            setImportance(saveImportance)
            if (currentItem.deadlineDate != null) {
                saveDeadlineDate = currentItem.deadlineDate
                binding.editDeadline.text = formatDate(currentItem.deadlineDate)
            }
        }
        binding.editDeleteBtn.isEnabled = true
        binding.editDeleteBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_red))
        binding.ivDelete.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_red))
        binding.editDeleteBtn.setOnClickListener {
            repository.removeItem(currentId)
            onDataUpdatedListener?.onDataRemove(currentId)
            findNavController().popBackStack()
        }
    }

    private fun initEditFields() {
        binding.editChangeImportance.setOnClickListener {
            showPopUpMenu()
        }
        binding.editSwitchDeadline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                showDatePickerDialog()
        }
    }

    private fun showPopUpMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.editChangeImportance)
        popupMenu.inflate(R.menu.importance)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            saveImportance = when (menuItem.itemId) {
                R.id.action_default -> {
                    Importance.DEFAULT
                }
                R.id.action_lower -> {
                    Importance.LOW
                }
                R.id.action_higher -> {
                    Importance.HIGH
                }
                else -> Importance.LOW
            }
            setImportance(saveImportance)
            true
        }
        popupMenu.show()
    }

    private fun setImportance(saveImportance: Importance) {
        when (saveImportance) {
            Importance.DEFAULT -> binding.editImportance.text = "Нет"
            Importance.LOW -> binding.editImportance.text = "Низкий"
            Importance.HIGH -> binding.editImportance.text = "!!Высокий"
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(), null,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Готово") { _, _ ->
            val year = datePickerDialog.datePicker.year - 1900
            val month = datePickerDialog.datePicker.month
            val dayOfMonth = datePickerDialog.datePicker.dayOfMonth
            saveDeadlineDate = Date(year, month, dayOfMonth)
            binding.editDeadline.text = formatDate(Date(year, month, dayOfMonth))
        }
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Отмена") { _, _ ->
            binding.editSwitchDeadline.isChecked = false
        }

        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.color_blue))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.color_blue))
    }

    private fun saveItem() {
        if (binding.editText.text.toString() == "") {
            Toast.makeText(requireActivity(), "Пожалуйста, напишите текст дела", Toast.LENGTH_SHORT).show()
            return
        }
        val todoItem = (if (currentId == "")
            false
        else
            repository.getItem(currentId)?.isDone)?.let {
            (if (currentId == "")
                UUID.randomUUID().toString()
            else
                repository.getItem(currentId)?.id)?.let { it1 ->
                TodoItem(
                    it1,
                    binding.editText.text.toString(),
                    saveImportance,
                    saveDeadlineDate,
                    it,
                    Date()
                )
            }
        }
        if (currentId == "")
            todoItem?.let { repository.addTodoItem(it) }
        else
            todoItem?.let { repository.updateItem(currentId, it) }
        onDataUpdatedListener?.onDataUpdated(currentId)
        findNavController().popBackStack()
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