package com.aalmoghalis.muhasibsoft.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aalmoghalis.muhasibsoft.data.local.ItemDao
import com.aalmoghalis.muhasibsoft.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * عامل خلفية يفحص المخزون دورياً
 * ويرسل إشعارات عند وصول الأصناف للحد الأدنى.
 */
@HiltWorker
class StockMonitorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val itemDao: ItemDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val lowStockItems = itemDao.getLowStockItems().first()
            lowStockItems.forEach { item ->
                NotificationHelper.showLowStockNotification(
                    context = applicationContext,
                    itemName = item.name,
                    currentQuantity = item.currentQuantity
                )
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "stock_monitor_work"
    }
}
