package com.aalmoghalis.muhasibsoft.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aalmoghalis.muhasibsoft.data.local.ItemDao
import com.aalmoghalis.muhasibsoft.utils.NotificationHelper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * عامل خلفية يفحص المخزون دورياً
 * ويرسل إشعارات عند وصول الأصناف للحد الأدنى
 */
class StockMonitorWorker @Inject constructor(
    private val context: Context,
    private val params: WorkerParameters,
    private val itemDao: ItemDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // جلب الأصناف منخفضة المخزون
            val lowStockItems = itemDao.getLowStockItems().first()

            // إرسال إشعار لكل صنف
            lowStockItems.forEach { item ->
                NotificationHelper.showLowStockNotification(
                    context = context,
                    itemName = item.name,
                    currentQuantity = item.currentQuantity
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "stock_monitor_work"
    }
}