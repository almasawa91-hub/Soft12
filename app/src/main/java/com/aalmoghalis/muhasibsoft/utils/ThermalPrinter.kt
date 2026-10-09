package com.aalmoghalis.muhasibsoft.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.aalmoghalis.muhasibsoft.data.model.Sale
import com.aalmoghalis.muhasibsoft.data.model.SaleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * نظام الطباعة الحرارية عبر Bluetooth
 * يدعم طابعات ESC/POS القياسية (عرض 58mm و 80mm)
 */
class ThermalPrinter {

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    /**
     * أوامر ESC/POS الأساسية
     */
    private object EscPos {
        val INIT = byteArrayOf(0x1B, 0x40)                    // تهيئة الطابعة
        val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)           // خط عريض
        val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)          // إيقاف الخط العريض
        val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)     // توسيط
        val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)       // محاذاة يسار
        val ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02)      // محاذاة يمين
        val FONT_DOUBLE = byteArrayOf(0x1D, 0x21, 0x11)      // خط مزدوج الحجم
        val FONT_NORMAL = byteArrayOf(0x1D, 0x21, 0x00)      // خط عادي
        val CUT_PAPER = byteArrayOf(0x1D, 0x56, 0x42, 0x00)  // قص الورق
        val FEED_LINES = byteArrayOf(0x1B, 0x64, 0x04)       // تغذية 4 أسطر
    }

    /**
     * الحصول على الأجهزة المقترنة
     */
    fun getPairedDevices(): List<BluetoothDevice> {
        return try {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            adapter?.bondedDevices?.toList() ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    /**
     * الاتصال بالطابعة
     */
    suspend fun connect(device: BluetoothDevice): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // UUID القياسي لطابعات Bluetooth Serial
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()
            outputStream = socket?.outputStream

            // تهيئة الطابعة
            outputStream?.write(EscPos.INIT)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * قطع الاتصال
     */
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            outputStream?.close()
            socket?.close()
        } catch (_: Exception) {
        }
    }

    /**
     * طباعة فاتورة بيع حرارية (58mm - 32 حرف بالسطر)
     */
    suspend fun printSaleInvoice(
        sale: Sale,
        items: List<SaleItem>,
        itemNames: Map<Long, String>,
        companyName: String,
        address: String? = null,
        phone: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val out = outputStream ?: return@withContext Result.failure(
                Exception("الطابعة غير متصلة")
            )

            val sb = StringBuilder()

            // رأس الفاتورة
            sb.append(escPos(EscPos.ALIGN_CENTER))
            sb.append(escPos(EscPos.BOLD_ON))
            sb.append(escPos(EscPos.FONT_DOUBLE))
            sb.append("$companyName\n")
            sb.append(escPos(EscPos.FONT_NORMAL))
            sb.append(escPos(EscPos.BOLD_OFF))

            address?.let { sb.append("$it\n") }
            phone?.let { sb.append("هاتف: $it\n") }

            sb.append("--------------------------------\n")

            // معلومات الفاتورة
            sb.append(escPos(EscPos.ALIGN_LEFT))
            sb.append("فاتورة: ${sale.invoiceNumber}\n")
            sb.append("التاريخ: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ar")).format(Date(sale.date))}\n")
            sb.append("النوع: ${if (sale.type == "cash") "نقدي" else "آجل"}\n")
            sb.append("--------------------------------\n")

            // الأصناف
            items.forEach { item ->
                val name = itemNames[item.itemId] ?: "صنف"
                sb.append("$name\n")
                sb.append(
                    String.format(
                        "  %.0f × %.2f = %.2f\n",
                        item.quantity, item.price, item.total
                    )
                )
            }

            sb.append("--------------------------------\n")

            // الإجماليات
            sb.append(String.format("المجموع: %.2f\n", sale.subtotal))
            if (sale.discount > 0) {
                sb.append(String.format("الخصم: %.2f\n", sale.discount))
            }
            if (sale.tax > 0) {
                sb.append(String.format("الضريبة: %.2f\n", sale.tax))
            }
            sb.append(escPos(EscPos.BOLD_ON))
            sb.append(String.format("الإجمالي: %.2f\n", sale.total))
            sb.append(escPos(EscPos.BOLD_OFF))

            if (sale.paid > 0) {
                sb.append(String.format("المدفوع: %.2f\n", sale.paid))
                sb.append(String.format("المتبقي: %.2f\n", sale.remaining))
            }

            // التذييل
            sb.append("--------------------------------\n")
            sb.append(escPos(EscPos.ALIGN_CENTER))
            sb.append("شكراً لتعاملكم معنا\n")
            sb.append("محاسب سوفت - 1.0.0\n")

            // إرسال البيانات
            out.write(sb.toString().toByteArray(charset("UTF-8")))
            out.write(EscPos.FEED_LINES)
            out.write(EscPos.CUT_PAPER)
            out.flush()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * طباعة نص خام
     */
    suspend fun printRawText(text: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            outputStream?.write(text.toByteArray(charset("UTF-8")))
            outputStream?.flush()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * اختبار الطباعة
     */
    suspend fun printTest(): Result<Unit> {
        return printRawText(
            buildString {
                append(escPos(EscPos.ALIGN_CENTER))
                append(escPos(EscPos.BOLD_ON))
                append("اختبار الطباعة\n")
                append(escPos(EscPos.BOLD_OFF))
                append("محاسب سوفت\n")
                append("--------------------------------\n")
                append("1234567890\n")
                append(escPos(EscPos.FEED_LINES.toString()))
            }
        )
    }

    /**
     * تحويل أوامر ESC/POS إلى نص قابل للإرسال
     */
    private fun escPos(command: ByteArray): String {
        return String(command, charset("ISO-8859-1"))
    }
}