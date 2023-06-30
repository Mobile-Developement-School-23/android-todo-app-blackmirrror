package ru.blackmirrror.todo.presentation.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.databinding.FragmentTodoItemsBinding
import ru.blackmirrror.todo.presentation.adapters.SwipeTodoItem
import ru.blackmirrror.todo.presentation.adapters.TodoItemAdapter


class TodoItemsFragment : Fragment(), TodoItemAdapter.RecyclerViewItemClickListener {

    private lateinit var binding: FragmentTodoItemsBinding
    private lateinit var showAllTodoItems: Drawable

    private lateinit var adapterApi: TodoItemAdapter
    private val todoItemsViewModel: TodoItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoItemsBinding.inflate(inflater, container, false)
        initFields()
        return binding.root
    }

    private fun initFields() {
        adapterApi = TodoItemAdapter(this)

        lifecycleScope.launch {
            todoItemsViewModel.tasks.collect { tasks ->
                adapterApi.setList(tasks)
            }
        }
        lifecycleScope.launch {
            todoItemsViewModel.countCompletedTask.collectLatest {
                binding.tvCount.text = "Выполнено - $it"
            }
        }

        binding.rvTodoItems.layoutManager = LinearLayoutManager(context)
        binding.rvTodoItems.adapter = adapterApi
        initSwipes(adapterApi)

        binding.floatingButton.setOnClickListener {
            val action = TodoItemsFragmentDirections.actionTodoItemsFragmentToEditTodoItemFragmentCreate()
            findNavController().navigate(action)
        }
        binding.toolbarMain.title = "Мои дела"
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
        todoItem.isDone = isChecked
        todoItemsViewModel.updateTask(todoItem)
    }

    override fun onItemClicked(id: String, todoItem: TodoItem) {
        Log.d("API", "onItemClicked: $todoItem")
        val action = TodoItemsFragmentDirections.actionTodoItemsFragmentToEditTodoItemFragmentCreate(todoItem)
        findNavController().navigate(action)
    }
}
