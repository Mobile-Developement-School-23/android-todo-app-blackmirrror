package ru.blackmirrror.todo.presentation.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("d MMMM yyyy г.", Locale("ru"))
        return sdf.format(date)
    }
}