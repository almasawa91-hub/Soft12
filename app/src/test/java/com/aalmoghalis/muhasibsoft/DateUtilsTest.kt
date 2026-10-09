package com.aalmoghalis.muhasibsoft

import com.aalmoghalis.muhasibsoft.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * اختبارات وحدة لأدوات التاريخ
 */
class DateUtilsTest {

    @Test
    fun `startOfDay returns midnight of the day`() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 8, 15, 30, 45)
        }

        val startOfDay = DateUtils.startOfDay(calendar.timeInMillis)

        val result = Calendar.getInstance().apply { timeInMillis = startOfDay }
        assertEquals(0, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, result.get(Calendar.MINUTE))
        assertEquals(0, result.get(Calendar.SECOND))
        assertEquals(2026, result.get(Calendar.YEAR))
        assertEquals(Calendar.OCTOBER, result.get(Calendar.MONTH))
        assertEquals(8, result.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `endOfDay returns last millisecond of the day`() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 8, 10, 0, 0)
        }

        val endOfDay = DateUtils.endOfDay(calendar.timeInMillis)

        val result = Calendar.getInstance().apply { timeInMillis = endOfDay }
        assertEquals(23, result.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, result.get(Calendar.MINUTE))
        assertEquals(59, result.get(Calendar.SECOND))
    }

    @Test
    fun `startOfMonth returns first day of month`() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 15)
        }

        val startOfMonth = DateUtils.startOfMonth(calendar.timeInMillis)

        val result = Calendar.getInstance().apply { timeInMillis = startOfMonth }
        assertEquals(1, result.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `startOfDay is before endOfDay`() {
        val now = System.currentTimeMillis()
        assertTrue(DateUtils.startOfDay(now) < DateUtils.endOfDay(now))
    }
}