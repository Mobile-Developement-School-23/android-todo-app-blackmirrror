package ru.blackmirrror.todo.presentation.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Date formatter change Date to String
 */

object Utils {
    fun fromDateToString(date: Date): String {
        val sdf = SimpleDateFormat("d MMMM yyyy г.", Locale("ru"))
        return sdf.format(date)
    }

    fun addOneDayToDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        Log.d("NOTIFY", "addOneDayToDate: ${calendar.time}")
        return calendar.time
    }
}
