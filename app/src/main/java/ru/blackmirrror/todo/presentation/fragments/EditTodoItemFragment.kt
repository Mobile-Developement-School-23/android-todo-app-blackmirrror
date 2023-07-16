package ru.blackmirrror.todo.presentation.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.models.Importance
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.databinding.FragmentEditTodoItemBinding
import ru.blackmirrror.todo.presentation.utils.Utils.fromDateToString
import java.util.Calendar
import java.util.Date
import java.util.UUID

/**
 * Add or edit fragment displays any item
 */

class EditTodoItemFragment : Fragment() {

    private lateinit var binding: FragmentEditTodoItemBinding

    private val todoItemsViewModel: TodoItemsViewModel by activityViewModels()

    private var saveImportance: Importance = Importance.BASIC
    private var currentTodoItem: TodoItem? = null
    private var saveDeadlineLong: Long? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTodoItemBinding.inflate(inflater, container, false)

        initEditFields()
        initToolbar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getString("todoItemId")?: ""
        if (id != "") {
            todoItemsViewModel.loadTaskById(id)

            lifecycleScope.launch {
                todoItemsViewModel.currentItem.collect { item ->
                    currentTodoItem = item
                    currentTodoItem?.let { fillFields(it) }
                }
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarEdit.title = ""
        binding.toolbarEdit.setNavigationIcon(R.drawable.baseline_close_24)
        binding.toolbarEdit.setNavigationOnClickListener {
            findNavController().popBackStack()
            todoItemsViewModel.initData()
        }
        binding.editSaveBtn.setOnClickListener {
            saveItem()
        }
    }

    private fun fillFields(currentTodoItem: TodoItem) {
        binding.editText.setText(currentTodoItem.text)
        saveImportance = currentTodoItem.importance
        setImportance(saveImportance)
        if (currentTodoItem.deadlineDate != null) {
            saveDeadlineLong = currentTodoItem.deadlineDate.time
            binding.editDeadline.text = fromDateToString(currentTodoItem.deadlineDate)
        }
        binding.editDeleteBtn.isEnabled = true
        binding.editDeleteBtn.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.color_red)
        )
        binding.ivDelete.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_red))
        binding.editDeleteBtn.setOnClickListener {
            showCancelableSnackbar(binding.editDeleteBtn, currentTodoItem)
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
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val dateTimePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                        val selectedDateTimeMillis = calendar.timeInMillis
                        saveDeadlineLong = selectedDateTimeMillis
                        Log.d("NOTIFY", "showDatePickerDialog: $saveDeadlineLong")
                        Log.d("NOTIFY", "showDatePickerDialog: ${Date(saveDeadlineLong!!)}")
                        binding.editDeadline.text = fromDateToString(Date(saveDeadlineLong!!))
                    },
                    currentHourOfDay,
                    currentMinute,
                    true
                )

                timePickerDialog.show()
                timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_blue))
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_blue))
            },
            currentYear,
            currentMonth,
            currentDayOfMonth
        )

        dateTimePickerDialog.show()
        dateTimePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_blue))
        dateTimePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.color_blue))
    }

    private fun saveItem() {
        if (binding.editText.text.toString() == "") {
            Toast.makeText(
                requireActivity(), "Пожалуйста, напишите текст дела", Toast.LENGTH_SHORT
            ).show()
            return
        }
        currentTodoItem?.let {
            todoItemsViewModel.updateTask(
                createTodoItem(
                    it.id,
                    it.createdDate,
                    it.isDone
                )
            )
        } ?: run {
            val task = createTodoItem(
                UUID.randomUUID().toString(),
                Date(),
                false
            )
            todoItemsViewModel.createTask(task)
        }
        findNavController().popBackStack()
    }

    private fun createTodoItem(id: String, dateOfCreated: Date?, done: Boolean): TodoItem {
        return TodoItem(
            id,
            binding.editText.text.toString(),
            saveImportance,
            saveDeadlineLong?.let { Date(it) },
            done,
            Date(),
            dateOfCreated
        )
    }

    private fun showCancelableSnackbar(view: View, todo: TodoItem) {
        val snackbar = Snackbar.make(view, "Удалить ${todo.text}", Snackbar.LENGTH_INDEFINITE)
            .setAction("Отменить") {
                binding.editDeleteBtn.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            }

        val snackbarView = snackbar.view
        val countdownDuration = 6000L
        val countdownInterval = 1000L

        val countDownTimer = object : CountDownTimer(countdownDuration, countdownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                snackbar.setText("Удалить ${todo.text} \nОсталось: $seconds сек")
            }
            override fun onFinish() {
                snackbar.dismiss()
                currentTodoItem?.let { todoItemsViewModel.deleteTask(it) }
                findNavController().popBackStack()
            }
        }
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onShown(sb: Snackbar?) {
                binding.editDeleteBtn.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.support_overlay))
                countDownTimer.start()
            }
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                countDownTimer.cancel()
            }
        })

        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_up)
        snackbarView.animation = animation

        snackbar.show()
    }

    companion object {
        private const val DEDUCTION_OF_THE_YEAR = 1900
    }
}