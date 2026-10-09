package com.aalmoghalis.muhasibsoft

import com.aalmoghalis.muhasibsoft.utils.NumberUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberUtilsTest {
    @Test
    fun formatNumberUsesTwoDecimalPlaces() {
        assertEquals("1234.57", NumberUtils.formatNumber(1234.5678))
        assertEquals("0.00", NumberUtils.formatNumber(0.0))
        assertEquals("100.50", NumberUtils.formatNumber(100.5))
        assertEquals("-2.25", NumberUtils.formatNumber(-2.25))
    }

    @Test
    fun validatesFiniteNumericStringsOnly() {
        assertTrue(NumberUtils.isValidNumber("123"))
        assertTrue(NumberUtils.isValidNumber("12.5"))
        assertFalse(NumberUtils.isValidNumber("abc"))
        assertFalse(NumberUtils.isValidNumber(""))
        assertFalse(NumberUtils.isValidNumber("NaN"))
        assertFalse(NumberUtils.isValidNumber("Infinity"))
    }

    @Test
    fun parseDoubleUsesDefaultForInvalidOrNonFiniteInput() {
        assertEquals(10.0, NumberUtils.parseDouble("10"), 0.001)
        assertEquals(0.0, NumberUtils.parseDouble("invalid"), 0.001)
        assertEquals(5.0, NumberUtils.parseDouble("", 5.0), 0.001)
        assertEquals(7.0, NumberUtils.parseDouble("NaN", 7.0), 0.001)
    }

    @Test
    fun calculateTaxAndDiscountHandleZeroRates() {
        assertEquals(15.0, NumberUtils.calculateTax(100.0, 15.0), 0.001)
        assertEquals(0.0, NumberUtils.calculateTax(100.0, 0.0), 0.001)
        assertEquals(7.5, NumberUtils.calculateTax(150.0, 5.0), 0.001)
        assertEquals(10.0, NumberUtils.calculateDiscount(100.0, 10.0), 0.001)
        assertEquals(25.0, NumberUtils.calculateDiscount(250.0, 10.0), 0.001)
        assertEquals(0.0, NumberUtils.calculateDiscount(250.0, 0.0), 0.001)
    }

    @Test
    fun formatsCurrencyWithExpectedSymbol() {
        assertEquals("12.50 ر.ي", NumberUtils.formatCurrency(12.5))
        assertEquals("12.50 $", NumberUtils.formatCurrency(12.5, "USD"))
    }
}
