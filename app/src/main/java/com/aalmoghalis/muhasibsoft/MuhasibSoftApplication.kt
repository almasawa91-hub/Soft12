package com.aalmoghalis.muhasibsoft

import android.app.Application
import androidx.work.*
import androidx.hilt.work.HiltWorkerFactory
import com.aalmoghalis.muhasibsoft.data.local.DatabaseSeeder
import com.aalmoghalis.muhasibsoft.utils.NotificationHelper
import com.aalmoghalis.muhasibsoft.workers.StockMonitorWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MuhasibSoftApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        // إنشاء قناة الإشعارات
        NotificationHelper.createNotificationChannel(this)

        // تهيئة قاعدة البيانات
        applicationScope.launch {
            databaseSeeder.seedIfEmpty()
        }

        // جدولة فحص المخزون كل 6 ساعات
        scheduleStockMonitor()
    }

    private fun scheduleStockMonitor() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val request = PeriodicWorkRequestBuilder<StockMonitorWorker>(
            6, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            StockMonitorWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}