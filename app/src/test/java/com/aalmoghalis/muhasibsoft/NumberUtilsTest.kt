package com.aalmoghalis.muhasibsoft

import com.aalmoghalis.muhasibsoft.utils.NumberUtils
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * اختبارات وحدة لأدوات الأرقام
 */
class NumberUtilsTest {

    @Test
    fun `formatNumber returns two decimal places`() {
        assertEquals("1234.57", NumberUtils.formatNumber(1234.5678))
        assertEquals("0.00", NumberUtils.formatNumber(0.0))
        assertEquals("100.50", NumberUtils.formatNumber(100.5))
    }

    @Test
    fun `isValidNumber validates numeric strings`() {
        assertEquals(true, NumberUtils.isValidNumber("123"))
        assertEquals(true, NumberUtils.isValidNumber("12.5"))
        assertEquals(false, NumberUtils.isValidNumber("abc"))
        assertEquals(false, NumberUtils.isValidNumber(""))
    }

    @Test
    fun `parseDouble returns default on invalid input`() {
        assertEquals(10.0, NumberUtils.parseDouble("10"), 0.001)
        assertEquals(0.0, NumberUtils.parseDouble("invalid"), 0.001)
        assertEquals(5.0, NumberUtils.parseDouble("", 5.0), 0.001)
    }

    @Test
    fun `calculateTax computes correct tax amount`() {
        assertEquals(15.0, NumberUtils.calculateTax(100.0, 15.0), 0.001)
        assertEquals(0.0, NumberUtils.calculateTax(100.0, 0.0), 0.001)
        assertEquals(7.5, NumberUtils.calculateTax(150.0, 5.0), 0.001)
    }

    @Test
    fun `calculateDiscount computes correct discount`() {
        assertEquals(10.0, NumberUtils.calculateDiscount(100.0, 10.0), 0.001)
        assertEquals(25.0, NumberUtils.calculateDiscount(250.0, 10.0), 0.001)
    }
}