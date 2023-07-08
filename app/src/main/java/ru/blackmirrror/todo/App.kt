package ru.blackmirrror.todo

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ru.blackmirrror.todo.presentation.utils.DataUpdateInitializer
import ru.blackmirrror.todo.presentation.utils.DataUpdateWorkerFactory
import javax.inject.Inject

@HiltAndroidApp
class App: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: DataUpdateWorkerFactory

    override fun onCreate() {
        super.onCreate()

        DataUpdateInitializer(this)
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
