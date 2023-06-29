package ru.blackmirrror.todo.presentation.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.models.Importance
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.databinding.FragmentEditTodoItemBinding
import ru.blackmirrror.todo.presentation.utils.Utils.formatDate
import java.util.Calendar
import java.util.Date
import java.util.UUID


class EditTodoItemFragment : Fragment() {

    private lateinit var binding: FragmentEditTodoItemBinding

    //private lateinit var repository: TodoItemRepository
    private val todoItemsViewModel: TodoItemsViewModel by activityViewModels()

    var onDataUpdatedListener: OnDataUpdatedListener? = null

    private var saveImportance: Importance = Importance.BASIC
    private var saveDeadlineDate: Date? = null
    //private lateinit var currentId: String
    private var currentTodoItem: TodoItem? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTodoItemBinding.inflate(inflater, container, false)

        //todoItemsViewModel = ViewModelProvider(this)[TodoItemsViewModel::class.java]
        //repository = TodoItemRepository.getInstance()
        initEditFields()
        initToolbar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: EditTodoItemFragmentArg = arguments?.let {
            EditTodoItemFragmentArg(it.getParcelable("todoItem"))
        } ?: throw IllegalArgumentException("Arguments not provided.")
        currentTodoItem = args.todoItem
        //val currentId = arguments?.getString("todoItemId", "").toString()
        lifecycleScope.launch {
            //currentTodoItem = todoItemsViewModel.getTodoItemById(currentId)
            Log.d("API", "onViewCreated: $currentTodoItem")
            currentTodoItem?.let { fillFields(it) }
        }
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
    }

    private fun fillFields(currentTodoItem: TodoItem) {
        Log.d("API", "fillFields: $currentTodoItem")
        binding.editText.setText(currentTodoItem.text)
        saveImportance = currentTodoItem.importance
        setImportance(saveImportance)
        if (currentTodoItem.deadlineDate != null) {
            saveDeadlineDate = currentTodoItem.deadlineDate
            binding.editDeadline.text = formatDate(currentTodoItem.deadlineDate)
        }
        binding.editDeleteBtn.isEnabled = true
        binding.editDeleteBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_red))
        binding.ivDelete.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_red))
        binding.editDeleteBtn.setOnClickListener {
            todoItemsViewModel.deleteTask(currentTodoItem, requireContext())
            //onDataUpdatedListener?.onDataRemove(currentId)
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
                    Importance.BASIC
                }
                R.id.action_lower -> {
                    Importance.LOW
                }
                R.id.action_higher -> {
                    Importance.IMPORTANT
                }
                else -> Importance.BASIC
            }
            setImportance(saveImportance)
            true
        }
        popupMenu.show()
    }

    private fun setImportance(saveImportance: Importance) {
        when (saveImportance) {
            Importance.BASIC -> binding.editImportance.text = "Нет"
            Importance.LOW -> binding.editImportance.text = "Низкий"
            Importance.IMPORTANT -> binding.editImportance.text = "!!Высокий"
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
        currentTodoItem?.let {
            Log.d("APO", "saveItem: ")
            Log.d("APO", "saveItem: $onDataUpdatedListener")
            onDataUpdatedListener?.onDataUpdated(
//                createTodoItem(
//                    currentTodoItem!!.id,
//                    currentTodoItem!!.createdDate,
//                    currentTodoItem!!.isDone
//                )
            "ddf"
            )
            todoItemsViewModel.updateTask(
                createTodoItem(
                    currentTodoItem!!.id,
                    currentTodoItem!!.createdDate,
                    currentTodoItem!!.isDone
                ),
                requireContext()
            )
        }?: run {
            todoItemsViewModel.createTask(
                createTodoItem(
                    UUID.randomUUID().toString(),
                    null,
                    false
                ),
                requireContext()
            )
            onDataUpdatedListener?.onDataSave(
                createTodoItem(
                    UUID.randomUUID().toString(),
                    null,
                    false
                )
            )
        }
        findNavController().popBackStack()
    }

    private fun createTodoItem(id: String, dateOfCreated: Date?, done: Boolean): TodoItem {
        return TodoItem(
            id,
            binding.editText.text.toString(),
            saveImportance,
            saveDeadlineDate,
            done,
            Date(),
            dateOfCreated
        )
    }

    interface OnDataUpdatedListener {
        fun onDataSave(todoItem: TodoItem)
        fun onDataUpdated(id: String)
        fun onDataRemove(todoItem: TodoItem)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onDataUpdatedListener = context as OnDataUpdatedListener
        } catch (_: ClassCastException) {
        }
    }
}