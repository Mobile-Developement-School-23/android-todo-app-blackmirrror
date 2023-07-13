package ru.blackmirrror.todo.presentation.utils.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.presentation.utils.Utils.addOneDayToDate
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class NotificationUpdateReceiver: BroadcastReceiver() {

    @Inject
    lateinit var repository: TodoRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NOTIFY", "onReceive: ")
        if (intent.action == "POSTPONE_ACTION") {
            val taskId = intent.getStringExtra("TASK_ID")

            CoroutineScope(Dispatchers.IO).launch {
                val curTask = taskId?.let { repository.getTodoItemById(it) }
                val newTask = curTask?.copy(deadlineDate = curTask.deadlineDate?.let {
                    addOneDayToDate(it)})
                if (newTask != null) {
                    repository.updateTask(newTask)
                }
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(taskId.hashCode())
        }
    }
}