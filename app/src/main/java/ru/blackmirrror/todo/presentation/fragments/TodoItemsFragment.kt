package ru.blackmirrror.todo.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.databinding.FragmentTodoItemsBinding
import ru.blackmirrror.todo.presentation.MainActivity
import ru.blackmirrror.todo.presentation.adapters.SwipeTodoItem
import ru.blackmirrror.todo.presentation.adapters.TodoItemAdapter
import java.util.Date

/**
 * Base TodoItems fragment displays all items
 */

class TodoItemsFragment : Fragment(), TodoItemAdapter.RecyclerViewItemClickListener {

    private lateinit var binding: FragmentTodoItemsBinding
    private var visibleDone: Boolean = false

    private lateinit var adapter: TodoItemAdapter
    private val todoItemsViewModel: TodoItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodoItemsBinding.inflate(inflater, container, false)
        val mainActivity = requireActivity() as MainActivity
        val taskId = mainActivity.getTaskIdFromIntent()
        Log.d("NOTIFY", "onCreate: $taskId")
        if (taskId != null) {
            val action =
                TodoItemsFragmentDirections.actionTodoItemsFragmentToEditTodoItemFragmentCreate(
                    taskId
                )
            findNavController().navigate(action)
        }
        initFields()
        return binding.root
    }

    private fun initFields() {
        adapter = TodoItemAdapter(this)

        lifecycleScope.launch {
            todoItemsViewModel.tasks.flowWithLifecycle(lifecycle).collect { tasks ->
                adapter.setList(tasks)
            }
        }
        lifecycleScope.launch {
            todoItemsViewModel.countCompletedTask.collectLatest {
                binding.tvCount.text = "Выполнено - $it"
            }
        }

        binding.ivVisible.setOnClickListener {
            visibleDone = if (visibleDone) {
                binding.ivVisible.setImageResource(R.drawable.ic_visibility_off)
                false
            } else {
                binding.ivVisible.setImageResource(R.drawable.ic_visibility_on)
                true
            }
        }

        binding.rvTodoItems.layoutManager = LinearLayoutManager(context)
        binding.rvTodoItems.adapter = adapter
        initSwipes(adapter)

        binding.floatingButton.setOnClickListener {
            val action = TodoItemsFragmentDirections.actionTodoItemsFragmentToEditTodoItemFragmentCreate("")
            findNavController().navigate(action)
        }
        binding.toolbarMain.title = "Мои дела"

        binding.brnSettings.setOnClickListener {
            val action = TodoItemsFragmentDirections.actionTodoItemsFragmentToSettingsFragment()
            findNavController().navigate(action)
        }
    }

    private fun initSwipes(adapter: TodoItemAdapter) {
        val swipeCallback = SwipeTodoItem(
            onSwipeLeft = { position ->
                todoItemsViewModel.deleteTask(adapter.getItem(position))
            },
            onSwipeRight = { position ->
                onCheckboxClicked( true, adapter.getItem(position))
            },
            applicationContext = requireContext()
        )

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvTodoItems)
    }

    override fun onCheckboxClicked(isChecked: Boolean, todoItem: TodoItem) {
        val newItem = TodoItem(todoItem.id, todoItem.text, todoItem.importance,
            todoItem.deadlineDate, isChecked, todoItem.createdDate, Date())
        todoItemsViewModel.updateTask(newItem)
    }

    override fun onItemClicked(id: String, todoItem: TodoItem) {
        Log.d("API", "onItemClicked: $todoItem")
        val action = TodoItemsFragmentDirections.actionTodoItemsFragmentToEditTodoItemFragmentCreate(todoItem.id)
        findNavController().navigate(action)
    }
}
