package com.aalmoghalis.muhasibsoft.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ar"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("ar"))

    /**
     * تنسيق التاريخ بالصيغة yyyy-MM-dd
     */
    fun formatDate(timestamp: Long): String = dateFormat.format(Date(timestamp))

    /**
     * تنسيق التاريخ والوقت
     */
    fun formatDateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))

    /**
     * تنسيق الوقت فقط
     */
    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))

    /**
     * الحصول على بداية اليوم
     */
    fun startOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * الحصول على نهاية اليوم
     */
    fun endOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    /**
     * الحصول على بداية الشهر
     */
    fun startOfMonth(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * الحصول على نهاية الشهر
     */
    fun endOfMonth(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.timeInMillis
    }
}