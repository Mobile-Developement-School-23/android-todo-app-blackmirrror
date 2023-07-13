package ru.blackmirrror.todo.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.databinding.FragmentSettingsBinding
import ru.blackmirrror.todo.presentation.MainActivity

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPrefs: SharedPrefs
    private val todoItemsViewModel: TodoItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        initFields()

        return binding.root
    }

    private fun initFields() {
        binding.toolbarSettings.setNavigationIcon(R.drawable.baseline_close_24)
        binding.toolbarSettings.setNavigationOnClickListener {
            findNavController().popBackStack()
            todoItemsViewModel.initData()
        }

        sharedPrefs = SharedPrefs(requireContext())
        val mainActivity = requireActivity() as MainActivity
    }
}