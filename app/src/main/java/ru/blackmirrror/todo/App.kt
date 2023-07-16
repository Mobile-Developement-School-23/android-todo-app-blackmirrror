package ru.blackmirrror.todo

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.presentation.utils.DataUpdateInitializer
import ru.blackmirrror.todo.presentation.utils.DataUpdateWorkerFactory
import ru.blackmirrror.todo.presentation.utils.notification.NotificationInitializer
import ru.blackmirrror.todo.presentation.utils.notification.NotificationWorkerFactory
import javax.inject.Inject

/**
 * Custom Application class sets worker and start point for DI
 */

@HiltAndroidApp
class App: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: DataUpdateWorkerFactory
    @Inject
    lateinit var notificationWorkerFactory: NotificationWorkerFactory
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreate() {
        super.onCreate()

        DataUpdateInitializer(this)
        if (sharedPrefs.getNotifications())
            NotificationInitializer(this)
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setWorkerFactory(notificationWorkerFactory)
            .build()
}
