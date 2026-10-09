package com.aalmoghalis.muhasibsoft.utils

import java.text.NumberFormat
import java.util.*

object NumberUtils {

    private val numberFormat = NumberFormat.getNumberInstance(Locale("ar"))

    /**
     * تنسيق الرقم بفواصل الآلاف
     */
    fun formatNumber(value: Double): String {
        return String.format("%.2f", value)
    }

    /**
     * تنسيق العملة
     */
    fun formatCurrency(value: Double, currency: String = "local"): String {
        val symbol = if (currency == "local") "ر.ي" else "$"
        return "${formatNumber(value)} $symbol"
    }

    /**
     * التحقق من صحة الرقم
     */
    fun isValidNumber(input: String): Boolean {
        return input.toDoubleOrNull() != null
    }

    /**
     * تحويل النص إلى رقم بأمان
     */
    fun parseDouble(input: String, default: Double = 0.0): Double {
        return input.toDoubleOrNull() ?: default
    }

    /**
     * حساب الضريبة
     */
    fun calculateTax(amount: Double, taxRate: Double): Double {
        return amount * taxRate / 100
    }

    /**
     * حساب الخصم بالنسبة المئوية
     */
    fun calculateDiscount(amount: Double, discountPercent: Double): Double {
        return amount * discountPercent / 100
    }
}