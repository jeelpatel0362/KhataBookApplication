package com.example.khatabookapplication.Entity

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        return displayFormat.format(Date(timestamp))
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
}