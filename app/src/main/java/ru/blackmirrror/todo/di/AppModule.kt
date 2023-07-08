package ru.blackmirrror.todo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.blackmirrror.todo.data.SharedPrefs
import ru.blackmirrror.todo.data.TodoRepository
import ru.blackmirrror.todo.data.api.ApiFactory
import ru.blackmirrror.todo.data.api.ApiService
import ru.blackmirrror.todo.data.local.TodoItemDb
import ru.blackmirrror.todo.presentation.fragments.ViewModelFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoItemDb(@ApplicationContext context: Context): TodoItemDb {
        return Room.databaseBuilder(context, TodoItemDb::class.java, "todo_items_db").build()
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiFactory.create()
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPrefs {
        return SharedPrefs(context)
    }

    @Provides
    fun provideRepository(@ApplicationContext context: Context, localDatabase: TodoItemDb,
                          apiService: ApiService): TodoRepository {
        return TodoRepository(context, localDatabase, apiService)
    }

    @Provides
    fun provideViewModelFactory(repository: TodoRepository): ViewModelFactoryImpl {
        return ViewModelFactoryImpl(repository)
    }
}
