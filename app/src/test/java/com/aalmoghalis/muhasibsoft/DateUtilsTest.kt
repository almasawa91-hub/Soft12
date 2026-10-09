package com.aalmoghalis.muhasibsoft

import com.aalmoghalis.muhasibsoft.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {
    @Test
    fun startOfDayReturnsMidnight() {
        val calendar = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 8, 15, 30, 45) }
        val result = Calendar.getInstance().apply { timeInMillis = DateUtils.startOfDay(calendar.timeInMillis) }
        assertEquals(0, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, result.get(Calendar.MINUTE))
        assertEquals(0, result.get(Calendar.SECOND))
        assertEquals(0, result.get(Calendar.MILLISECOND))
        assertEquals(2026, result.get(Calendar.YEAR))
        assertEquals(Calendar.OCTOBER, result.get(Calendar.MONTH))
        assertEquals(8, result.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun endOfDayReturnsLastMillisecond() {
        val timestamp = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 8, 10, 0, 0) }.timeInMillis
        val result = Calendar.getInstance().apply { timeInMillis = DateUtils.endOfDay(timestamp) }
        assertEquals(23, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, result.get(Calendar.MINUTE))
        assertEquals(59, result.get(Calendar.SECOND))
        assertEquals(999, result.get(Calendar.MILLISECOND))
    }

    @Test
    fun startOfMonthResetsTimeAndStartsOnFirstDay() {
        val timestamp = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 15, 12, 45, 30)
            set(Calendar.MILLISECOND, 789)
        }.timeInMillis
        val result = Calendar.getInstance().apply { timeInMillis = DateUtils.startOfMonth(timestamp) }
        assertEquals(1, result.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, result.get(Calendar.MINUTE))
        assertEquals(0, result.get(Calendar.SECOND))
        assertEquals(0, result.get(Calendar.MILLISECOND))
    }

    @Test
    fun endOfMonthIncludesLastDayAndMillisecond() {
        val timestamp = Calendar.getInstance().apply { set(2024, Calendar.FEBRUARY, 10, 12, 0, 0) }.timeInMillis
        val result = Calendar.getInstance().apply { timeInMillis = DateUtils.endOfMonth(timestamp) }
        assertEquals(29, result.get(Calendar.DAY_OF_MONTH))
        assertEquals(23, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, result.get(Calendar.MINUTE))
        assertEquals(59, result.get(Calendar.SECOND))
        assertEquals(999, result.get(Calendar.MILLISECOND))
    }

    @Test
    fun startOfDayIsBeforeEndOfDay() {
        val now = System.currentTimeMillis()
        assertTrue(DateUtils.startOfDay(now) < DateUtils.endOfDay(now))
    }
}
