package ru.blackmirrror.todo.presentation.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Date formatter change Date to String
 */

object Utils {
    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("d MMMM yyyy г.", Locale("ru"))
        return sdf.format(date)
    }
}
