package com.aalmoghalis.muhasibsoft.utils

import android.content.Context
import java.io.File
import java.io.FileWriter

/**
 * أداة تصدير البيانات إلى CSV (يفتح في Excel)
 * بديل خفيف عن مكتبة Apache POI لتجنب مشاكل الحجم
 */
object CsvExporter {

    /**
     * تصدير جدول إلى ملف CSV
     */
    fun exportToCsv(
        context: Context,
        fileName: String,
        headers: List<String>,
        rows: List<List<String>>
    ): Result<File> {
        return try {
            val file = File(context.getExternalFilesDir(null), "$fileName.csv")

            FileWriter(file).use { writer ->
                // BOM لدعم العربية في Excel
                writer.write('\uFEFF'.toString())

                // الرؤوس
                writer.writeLine(headers.joinToString(","))

                // الصفوف
                rows.forEach { row ->
                    writer.writeLine(
                        row.joinToString(",") { cell ->
                            // حماية الخلايا التي تحتوي على فواصل
                            if (cell.contains(",") || cell.contains("\"")) {
                                "\"${cell.replace("\"", "\"\"")}\""
                            } else {
                                cell
                            }
                        }
                    )
                }
            }

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تصدير الأصناف
     */
    fun exportItems(
        context: Context,
        items: List<Triple<String, Double, Double>> // الاسم، الكمية، السعر
    ): Result<File> {
        return exportToCsv(
            context = context,
            fileName = "items_${System.currentTimeMillis()}",
            headers = listOf("اسم الصنف", "الكمية", "سعر البيع"),
            rows = items.map { (name, qty, price) ->
                listOf(name, qty.toString(), price.toString())
            }
        )
    }
}