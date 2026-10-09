package com.aalmoghalis.muhasibsoft.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object BackupUtils {

    /**
     * إنشاء نسخة احتياطية من قاعدة البيانات
     */
    fun createBackup(context: Context, destinationUri: Uri): Result<String> {
        return try {
            val dbFile = context.getDatabasePath("muhasib_soft.db")
            if (!dbFile.exists()) {
                return Result.failure(Exception("قاعدة البيانات غير موجودة"))
            }

            context.contentResolver.openOutputStream(destinationUri)?.use { output ->
                FileInputStream(dbFile).use { input ->
                    input.copyTo(output)
                }
            }

            Result.success("تم إنشاء النسخة الاحتياطية بنجاح")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * استرجاع نسخة احتياطية
     */
    fun restoreBackup(context: Context, sourceUri: Uri): Result<String> {
        return try {
            val dbFile = context.getDatabasePath("muhasib_soft.db")

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }

            Result.success("تم استرجاع النسخة الاحتياطية بنجاح")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * توليد اسم ملف للنسخة الاحتياطية
     */
    fun generateBackupFileName(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        return "muhasib_backup_${dateFormat.format(Date())}.db"
    }
}