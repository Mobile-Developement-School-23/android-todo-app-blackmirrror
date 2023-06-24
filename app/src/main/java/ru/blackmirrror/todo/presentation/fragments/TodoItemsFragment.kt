package ru.blackmirrror.todo.presentation.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.TodoItemRepository
import ru.blackmirrror.todo.databinding.FragmentTodoItemsBinding
import ru.blackmirrror.todo.presentation.adapters.SwipeTodoItem
import ru.blackmirrror.todo.presentation.adapters.TodoItemAdapter


class TodoItemsFragment : Fragment(), TodoItemAdapter.RecyclerViewItemClickListener,
    EditTodoItemFragment.OnDataUpdatedListener{

    private lateinit var binding: FragmentTodoItemsBinding
    private lateinit var showAllTodoItems: Drawable

    private lateinit var adapter: TodoItemAdapter
    private lateinit var repository: TodoItemRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoItemsBinding.inflate(inflater, container, false)
        initFields()
        //initSwipes(adapter)

        return binding.root
    }

    private fun initFields() {
        repository = TodoItemRepository.getInstance()

        binding.rvTodoItems.layoutManager = LinearLayoutManager(context)
        adapter = TodoItemAdapter(repository.getAllTodoItems(), this)
        binding.rvTodoItems.adapter = adapter

        binding.floatingButton.setOnClickListener {
            val action = TodoItemsFragmentDirections.actionTodoItemsFragmentToEditTodoItemFragmentCreate()
            findNavController().navigate(action)
        }
        binding.toolbarMain.title = "Мои дела"
        binding.tvCount.text = "Выполнено - ${repository.getDoneTodoItems().size}"
    }

    private fun initSwipes(adapter: TodoItemAdapter) {
        val swipeCallback = SwipeTodoItem(
            onSwipeLeft = { position ->
                val id = adapter.getItem(position).id
                onCheckboxClicked(id, true)
            },
            onSwipeRight = { position ->
                val id = adapter.getItem(position).id
                onDataUpdated(id)
            },
            applicationContext = requireContext()
        )

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvTodoItems)
    }

    override fun onCheckboxClicked(id: String, isChecked: Boolean) {
        adapter.getItem(id)?.isDone = isChecked
        adapter.getItem(id)?.let { repository.updateItem(id, it) }
        adapter.getItem(id)?.let { adapter.updateItem(id, it) }
        binding.tvCount.text = "Выполнено - ${repository.getDoneTodoItems().size}"
        //TODO зачеркивать item
    }

    override fun onItemClicked(id: String) {
        val action = TodoItemsFragmentDirections.actionTodoItemsFragmentToEditTodoItemFragmentCreate(id)
        findNavController().navigate(action)
    }

    override fun onDataUpdated(id: String) {
        if (id != "")
            repository.getItem(id)?.let { adapter.updateItem(id, it) }
        else
            repository.getItem(id)?.let { adapter.addNewItem(it) }
    }

    override fun onDataRemove(id: String) {
        adapter.removeItem(id)
        repository.removeItem(id)
    }
}