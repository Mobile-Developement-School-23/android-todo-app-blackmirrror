package ru.blackmirrror.todo.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.local.TodoItemDb
import ru.blackmirrror.todo.presentation.fragments.TodoItemsViewModel
import ru.blackmirrror.todo.presentation.fragments.ViewModelFactory
import ru.blackmirrror.todo.presentation.utils.DataUpdateWorker
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TodoItemsViewModel
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelFactory = ViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory)[TodoItemsViewModel::class.java]
        setContentView(R.layout.activity_main)
        schedulePeriodicWork()

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val workRequest = OneTimeWorkRequestBuilder<DataUpdateWorker>()
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(workRequest)
                Toast.makeText(applicationContext, "Соединение восстановлено", Toast.LENGTH_SHORT).show()
            }
        }
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }

    private fun schedulePeriodicWork() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<DataUpdateWorker>(8, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest).result.addListener(
            {
                Log.d("WorkManager", "WorkManager result: Success")
            }, Executors.newSingleThreadExecutor())
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}