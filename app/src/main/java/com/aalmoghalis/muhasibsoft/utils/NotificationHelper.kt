package com.aalmoghalis.muhasibsoft.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.aalmoghalis.muhasibsoft.MainActivity
import com.aalmoghalis.muhasibsoft.R

/**
 * مساعد إنشاء وإدارة الإشعارات
 */
object NotificationHelper {

    private const val CHANNEL_ID = "muhasib_soft_channel"
    private const val CHANNEL_NAME = "إشعارات محاسب سوفت"
    private const val LOW_STOCK_NOTIFICATION_ID = 1001

    /**
     * إنشاء قناة الإشعارات (Android 8+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات المخزون والعمليات"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * إشعار نقص المخزون
     */
    fun showLowStockNotification(
        context: Context,
        itemName: String,
        currentQuantity: Double
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("تنبيه نقص مخزون")
            .setContentText("الصنف: $itemName - الكمية المتبقية: $currentQuantity")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("وصل الصنف \"$itemName\" إلى الحد الأدنى من المخزون ($currentQuantity). يرجى إعادة التوريد.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(LOW_STOCK_NOTIFICATION_ID + itemName.hashCode(), notification)
    }

    /**
     * إشعار عام
     */
    fun showGeneralNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}