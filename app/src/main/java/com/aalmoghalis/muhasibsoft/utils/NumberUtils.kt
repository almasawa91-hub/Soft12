package com.aalmoghalis.muhasibsoft.utils

import java.text.NumberFormat
import java.util.Locale

object NumberUtils {
    @Suppress("unused")
    private val numberFormat = NumberFormat.getNumberInstance(Locale("ar"))

    /** تنسيق الرقم بمنزلتين عشريتين. */
    fun formatNumber(value: Double): String = String.format(Locale.US, "%.2f", value)

    /** تنسيق العملة. */
    fun formatCurrency(value: Double, currency: String = "local"): String {
        val symbol = if (currency == "local") "ر.ي" else "$"
        return "${formatNumber(value)} $symbol"
    }

    /** التحقق من أن النص يمثل رقماً محدوداً صالحاً. */
    fun isValidNumber(input: String): Boolean =
        input.toDoubleOrNull()?.isFinite() == true

    /** تحويل النص إلى رقم بأمان. */
    fun parseDouble(input: String, default: Double = 0.0): Double =
        input.toDoubleOrNull()?.takeIf { it.isFinite() } ?: default

    fun calculateTax(amount: Double, taxRate: Double): Double =
        amount * taxRate / 100

    fun calculateDiscount(amount: Double, discountPercent: Double): Double =
        amount * discountPercent / 100
}
