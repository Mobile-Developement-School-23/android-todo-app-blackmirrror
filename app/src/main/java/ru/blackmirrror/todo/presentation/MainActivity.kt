package ru.blackmirrror.todo.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.presentation.fragments.TodoItemsFragmentDirections
import ru.blackmirrror.todo.presentation.fragments.TodoItemsViewModel
import ru.blackmirrror.todo.presentation.fragments.ViewModelFactoryImpl
import ru.blackmirrror.todo.presentation.utils.DataUpdateWorker
import javax.inject.Inject

/**
 * Activity set up workManager for network state control
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactoryImpl: ViewModelFactoryImpl
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private lateinit var viewModel: TodoItemsViewModel
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactoryImpl)[TodoItemsViewModel::class.java]

        changeTheme(sharedPrefs.getTheme())
        setContentView(R.layout.activity_main)
        setUpConnectivityManager()
        checkNotifications()
    }

    fun getTaskIdFromIntent(): String? {
        val taskId =  intent.getStringExtra("taskId")
        intent.removeExtra("taskId")
        return taskId
    }

    fun changeTheme(value: Int) {
        when (value) {
            (0) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.AppTheme)
                sharedPrefs.putTheme(0)
            }
            (1) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.AppTheme)
                sharedPrefs.putTheme(1)
            }
            (2) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                setTheme(R.style.AppTheme_System)
                sharedPrefs.putTheme(2)
            }
        }
    }

    private fun checkNotifications() {
        if (!sharedPrefs.getNotifications())
            showDialog()
    }

    private fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Разрешение на отправку уведомлений")
        dialogBuilder.setMessage("Можем ли мы отправлять вам уведомления?")
        dialogBuilder.setPositiveButton("Да") { dialog, _ ->
            sharedPrefs.putNotifications(true)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Нет") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun setUpConnectivityManager() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val workRequest = OneTimeWorkRequestBuilder<DataUpdateWorker>()
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(workRequest)
                Toast.makeText(
                    applicationContext, "Соединение восстановлено", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        connectivityManager
            .registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
