package ru.blackmirrror.todo.presentation.utils.notification

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.data.models.TodoItem
import java.util.Calendar
import java.util.Date

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: TodoRepository,
    private val sharedPrefs: SharedPrefs): Worker(context, params) {

    override fun doWork(): Result {
        checkDeadlines()
        return Result.success()
    }

    private fun checkDeadlines() {
        repository.checkDeadlines()
//        val tasks = repository.getAllTodoItemsNoFlow()
//        Log.d("NOTIFY", "checkDeadlines: $tasks")
//
//        val currentDate = Calendar.getInstance().time
//
//        for (task in tasks) {
//            val deadline = task.deadlineDate
//            if (deadline != null) {
//                if (isSameDay(currentDate, deadline)) {
//                    scheduleNotification(task)
//                    //Log.d("NOTIFY", "checkDeadlines: $deadline")
//                }
//            }
//        }
    }

    private fun scheduleNotification(task: TodoItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(applicationContext, NotificationReceiver::class.java)
        notificationIntent.putExtra("taskId", task.id)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            task.id.hashCode(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        task.deadlineDate?.let { alarmManager.setExact(AlarmManager.RTC_WAKEUP, it.time, pendingIntent) }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date1
        val calendar2 = Calendar.getInstance()
        calendar2.time = date2
        return calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
                && calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
    }
}