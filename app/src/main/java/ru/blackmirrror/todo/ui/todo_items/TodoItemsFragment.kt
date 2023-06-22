package ru.blackmirrror.todo.ui.todo_items

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.TodoItemRepository
import ru.blackmirrror.todo.ui.EditTodoItemFragment


class TodoItemsFragment : Fragment(), TodoItemAdapter.RecyclerViewItemClickListener,
    EditTodoItemFragment.OnDataUpdatedListener{

    private lateinit var view: View
    private lateinit var toolbar: Toolbar
    private lateinit var showAllTodoItems: Drawable

    private lateinit var items: RecyclerView
    private lateinit var adapter: TodoItemAdapter
    private lateinit var addItem: FloatingActionButton

    private lateinit var repository: TodoItemRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view =  inflater.inflate(R.layout.fragment_todo_items, container, false)
        initFields()

        return view
    }

    private fun initFields() {
        repository = TodoItemRepository.getInstance()

        items = view.findViewById(R.id.rv_todo_items)
        items.layoutManager = LinearLayoutManager(context)
        adapter = TodoItemAdapter(repository.getAllTodoItems(), this)
        items.adapter = adapter

        addItem = view.findViewById(R.id.main_add_btn)
        addItem.setOnClickListener {
            replaceFragment(EditTodoItemFragment())
        }

        toolbar = view.findViewById(R.id.toolbar_main)
        showAllTodoItems = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_remove_red_eye_24)!!
        changeColor(R.color.color_light_gray_light)
        toolbar.navigationIcon = showAllTodoItems
        toolbar.subtitle = "Выполнено - ${repository.getDoneTodoItems().size}"
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = fragmentManager?.beginTransaction() ?: return
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun changeColor(color: Int) {
        showAllTodoItems.setColorFilter(ContextCompat.getColor(requireContext(), color), PorterDuff.Mode.SRC_IN)
    }

    override fun onCheckboxClicked(id: String, isChecked: Boolean) {
        adapter.getItem(id)?.isDone = isChecked
        adapter.getItem(id)?.let { repository.updateItem(id, it) }
        adapter.getItem(id)?.let { adapter.updateItem(id, it) }
        toolbar.subtitle = "Выполнено - ${repository.getDoneTodoItems().size}"
    }

    override fun onImageButtonClicked(id: String) {
        val bundle = Bundle()
        bundle.putString("id", id)
        val editFragment = EditTodoItemFragment()
        editFragment.arguments = bundle
        editFragment.onDataUpdatedListener = this
        replaceFragment(editFragment)
    }

    override fun onDataUpdated(id: String) {
        if (id != "")
            repository.getItem(id)?.let { adapter.updateItem(id, it) }
        else
            repository.getItem(id)?.let { adapter.addNewItem(it) }
    }

    override fun onDataRemove(id: String) {
        adapter.removeItem(id)
    }
}