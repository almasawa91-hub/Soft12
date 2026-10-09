package com.aalmoghalis.muhasibsoft.utils

import android.graphics.Bitmap
import android.graphics.Color

/**
 * مولد باركود بسيط (Code 128) بدون مكتبات خارجية
 * لرسم الباركود على Bitmap للطباعة أو العرض
 */
object BarcodeGenerator {

    // أنماط Code 128 (مبسطة للأرقام والحروف الإنجليزية)
    private val CODE128_PATTERNS = mapOf(
        '0' to "1010011000", '1' to "1001011000", '2' to "1001001100",
        '3' to "1011001000", '4' to "1001101000", '5' to "1001100100",
        '6' to "1010010001", '7' to "1001010001", '8' to "1001000101",
        '9' to "1101001000", 'A' to "1011010000", 'B' to "1001101000",
        'C' to "1101010000", 'D' to "1100101000", 'E' to "1011001000"
    )

    private const val START_CODE = "11010010000"
    private const val STOP_CODE = "1100011101011"

    /**
     * توليد باركود من نص
     * @param content النص المراد ترميزه
     * @param width عرض الباركود بالبكسل
     * @param height ارتفاع الباركود بالبكسل
     */
    fun generateBarcode(
        content: String,
        width: Int = 300,
        height: Int = 100
    ): Bitmap? {
        return try {
            // بناء سلسلة البتات
            val bits = StringBuilder(START_CODE)

            content.uppercase().forEach { char ->
                CODE128_PATTERNS[char]?.let { bits.append(it) }
            }

            bits.append(STOP_CODE)

            val bitString = bits.toString()
            val barWidth = width.toFloat() / bitString.length

            // إنشاء Bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                val bitIndex = (x / barWidth).toInt().coerceIn(0, bitString.length - 1)
                val color = if (bitString[bitIndex] == '1') Color.BLACK else Color.WHITE

                for (y in 0 until height) {
                    bitmap.setPixel(x, y, color)
                }
            }

            bitmap
        } catch (e: Exception) {
            null
        }
    }

    /**
     * توليد رقم باركود تلقائي فريد
     */
    fun generateBarcodeNumber(): String {
        val timestamp = System.currentTimeMillis().toString()
        return timestamp.takeLast(12)
    }

    /**
     * التحقق من صحة رقم الباركود (EAN-13 checksum)