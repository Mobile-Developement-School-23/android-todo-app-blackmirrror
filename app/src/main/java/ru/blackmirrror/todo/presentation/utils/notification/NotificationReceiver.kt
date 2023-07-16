package ru.blackmirrror.todo.presentation.utils.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ru.blackmirrror.todo.R
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.data.models.Importance
import ru.blackmirrror.todo.data.models.TodoItem
import ru.blackmirrror.todo.presentation.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver: BroadcastReceiver() {

    @Inject
    lateinit var repository: TodoRepository

    companion object {
        private const val CHANNEL_ID = "TaskChannel"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent?.getStringExtra("taskId")?: ""

        val task = runBlocking(Dispatchers.IO) {
            repository.getTodoItemById(taskId)
        }

//        val bundle = Bundle().apply {
//            putString("taskId", taskId)
//        }
//
//        val destinationIntent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            Log.d("NOTIFY", "onReceive: $bundle")
//            putExtras(bundle)
//        }

        val destinationIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("taskId", taskId)
            Log.d("NOTIFY", "onReceive: $taskId")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            destinationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val postponeUpdateIntent = Intent(context, NotificationUpdateReceiver::class.java).apply {
            action = "POSTPONE_ACTION"
            putExtra("TASK_ID", task?.id)
        }

        val postponeUpdatePendingIntent = PendingIntent.getBroadcast(
            context,
            task?.id.hashCode(),
            postponeUpdateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val postponeUpdateAction = NotificationCompat.Action.Builder(
            R.drawable.baseline_close_24,
            "Отложить на день",
            postponeUpdatePendingIntent
        ).build()

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        if (task != null) {
            notificationManager.cancel(task.id.hashCode())
            Log.d("NF", "onReceive: delete ")
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(supportIcon(task))
                .setContentTitle("Напоминание о дедлайне")
                .setContentText(task.text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(postponeUpdateAction)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            notificationManager.notify(task.id.hashCode(), builder.build())
        }
    }

    private fun supportIcon(task: TodoItem?): Int {
        if (task != null) {
            return when (task.importance) {
                (Importance.IMPORTANT) -> R.drawable.ic_importance_high
                (Importance.LOW) -> R.drawable.ic_importance_low
                else -> R.drawable.ic_info
            }
        }
        return R.drawable.ic_info
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Notifications"
            val descriptionText = "Notification Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}