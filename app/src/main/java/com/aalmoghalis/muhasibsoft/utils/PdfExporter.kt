package com.aalmoghalis.muhasibsoft.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.aalmoghalis.muhasibsoft.data.model.Sale
import com.aalmoghalis.muhasibsoft.data.model.SaleItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * أداة تصدير الفواتير والتقارير إلى PDF
 * باستخدام Android PdfDocument الأصلي (بدون مكتبات خارجية)
 */
object PdfExporter {

    private const val PAGE_WIDTH = 595   // A4 عرض
    private const val PAGE_HEIGHT = 842  // A4 ارتفاع
    private const val MARGIN = 40f
    private const val LINE_HEIGHT = 24f

    /**
     * تصدير فاتورة بيع إلى PDF
     */
    fun exportSaleInvoice(
        context: Context,
        sale: Sale,
        items: List<SaleItem>,
        itemNames: Map<Long, String>,
        companyName: String = "محاسب سوفت"
    ): Result<File> {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val titlePaint = Paint().apply {
                textSize = 20f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }

            val headerPaint = Paint().apply {
                textSize = 14f
                isFakeBoldText = true
            }

            val textPaint = Paint().apply {
                textSize = 12f
            }

            var y = MARGIN + 20

            // عنوان الشركة
            canvas.drawText(companyName, PAGE_WIDTH / 2f, y, titlePaint)
            y += LINE_HEIGHT

            canvas.drawText("فاتورة بيع", PAGE_WIDTH / 2f, y, titlePaint)
            y += LINE_HEIGHT * 1.5f

            // معلومات الفاتورة
            canvas.drawText("رقم الفاتورة: ${sale.invoiceNumber}", MARGIN, y, headerPaint)
            y += LINE_HEIGHT

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
            canvas.drawText("التاريخ: ${dateFormat.format(Date(sale.date))}", MARGIN, y, headerPaint)
            y += LINE_HEIGHT

            canvas.drawText(
                "نوع البيع: ${if (sale.type == "cash") "نقدي" else "آجل"}",
                MARGIN, y, headerPaint
            )
            y += LINE_HEIGHT * 1.5f

            // رأس جدول الأصناف
            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, headerPaint)
            y += LINE_HEIGHT

            canvas.drawText("الصنف", MARGIN, y, headerPaint)
            canvas.drawText("الكمية", PAGE_WIDTH / 2f, y, headerPaint)
            canvas.drawText("السعر", PAGE_WIDTH * 0.7f, y, headerPaint)
            canvas.drawText("الإجمالي", PAGE_WIDTH - MARGIN - 50, y, headerPaint)
            y += LINE_HEIGHT

            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, headerPaint)
            y += LINE_HEIGHT

            // صفوف الأصناف
            items.forEach { item ->
                canvas.drawText(
                    itemNames[item.itemId] ?: "-",
                    MARGIN, y, textPaint
                )
                canvas.drawText(
                    String.format("%.0f", item.quantity),
                    PAGE_WIDTH / 2f, y, textPaint
                )
                canvas.drawText(
                    String.format("%.2f", item.price),
                    PAGE_WIDTH * 0.7f, y, textPaint
                )
                canvas.drawText(
                    String.format("%.2f", item.total),
                    PAGE_WIDTH - MARGIN - 50, y, textPaint
                )
                y += LINE_HEIGHT
            }

            y += LINE_HEIGHT
            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, headerPaint)
            y += LINE_HEIGHT * 1.2f

            // الإجماليات
            canvas.drawText("المجموع: ${String.format("%.2f", sale.subtotal)}",
                PAGE_WIDTH - MARGIN - 150, y, headerPaint)
            y += LINE_HEIGHT

            canvas.drawText("الضريبة: ${String.format("%.2f", sale.tax)}",
                PAGE_WIDTH - MARGIN - 150, y, headerPaint)
            y += LINE_HEIGHT

            canvas.drawText("الخصم: ${String.format("%.2f", sale.discount)}",
                PAGE_WIDTH - MARGIN - 150, y, headerPaint)
            y += LINE_HEIGHT

            canvas.drawText("الإجمالي النهائي: ${String.format("%.2f", sale.total)}",
                PAGE_WIDTH - MARGIN - 150, y, headerPaint)
            y += LINE_HEIGHT * 1.5f

            // تذييل
            canvas.drawText(
                "شكراً لتعاملكم معنا",
                PAGE_WIDTH / 2f,
                PAGE_HEIGHT - MARGIN,
                titlePaint
            )

            document.finishPage(page)

            // حفظ الملف
            val fileName = "invoice_${sale.invoiceNumber.replace("#", "")}.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)

            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تصدير تقرير عام إلى PDF
     */
    fun exportReport(
        context: Context,
        title: String,
        headers: List<String>,
        rows: List<List<String>>,
        fileName: String
    ): Result<File> {
        return try {
            val document = PdfDocument()
            var pageNumber = 1
            var page = document.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            )
            var canvas = page.canvas

            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }

            val headerPaint = Paint().apply {
                textSize = 12f
                isFakeBoldText = true
            }

            val textPaint = Paint().apply {
                textSize = 11f
            }

            var y = MARGIN + 20

            // العنوان
            canvas.drawText(title, PAGE_WIDTH / 2f, y, titlePaint)
            y += LINE_HEIGHT * 1.5f

            // الرؤوس
            val columnWidth = (PAGE_WIDTH - 2 * MARGIN) / headers.size
            headers.forEachIndexed { index, header ->
                canvas.drawText(header, MARGIN + index * columnWidth, y, headerPaint)
            }
            y += LINE_HEIGHT
            canvas.drawLine(MARGIN, y - 8, PAGE_WIDTH - MARGIN, y - 8, headerPaint)

            // الصفوف
            rows.forEach { row ->
                // صفحة جديدة إذا امتلأت
                if (y > PAGE_HEIGHT - MARGIN) {
                    document.finishPage(page)
                    pageNumber++
                    page = document.startPage(
                        PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                    )
                    canvas = page.canvas
                    y = MARGIN
                }

                y += LINE_HEIGHT
                row.forEachIndexed { index, cell ->
                    canvas.drawText(cell, MARGIN + index * columnWidth, y, textPaint)
                }
            }

            document.finishPage(page)

            val file = File(context.getExternalFilesDir(null), fileName)
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}