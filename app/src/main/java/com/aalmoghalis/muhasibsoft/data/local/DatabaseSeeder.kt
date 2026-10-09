package com.aalmoghalis.muhasibsoft.data.local

import com.aalmoghalis.muhasibsoft.data.model.*
import com.aalmoghalis.muhasibsoft.utils.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** تهيئة بيانات الإعداد الضرورية فقط عند أول تشغيل. */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val db: AppDatabase
) {
    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        val users = db.userDao().getAllUsers().first()
        if (users.isNotEmpty()) return@withContext

        // بيانات الدخول الأولية: admin / 1234. غيّر كلمة المرور بعد أول تسجيل دخول.
        db.userDao().insertUser(
            User(
                name = "مدير النظام",
                username = "admin",
                password = PasswordUtils.hash("1234"),
                role = "admin"
            )
        )
        db.warehouseDao().insertWarehouse(Warehouse(name = "المخزن الرئيسي", isMain = true))
        db.currencyDao().insertCurrency(
            Currency(name = "محلي", symbol = "ر.ي", code = "YR", exchangeRate = 1.0, isDefault = true)
        )
        db.currencyDao().insertCurrency(
            Currency(name = "دولار", symbol = "$", code = "USD", exchangeRate = 500.0)
        )
        listOf(
            Unit(name = "حبة", abbreviation = "حبة"),
            Unit(name = "كيلو", abbreviation = "كيلو"),
            Unit(name = "كرتون", abbreviation = "كرتون"),
            Unit(name = "كيس", abbreviation = "كيس")
        ).forEach { db.unitDao().insertUnit(it) }
        listOf(
            Category(name = "عام", type = "general"),
            Category(name = "الصندوق", type = "asset"),
            Category(name = "المشتريات", type = "expense"),
            Category(name = "المبيعات", type = "income"),
            Category(name = "الموردون", type = "liability"),
            Category(name = "العملاء", type = "asset")
        ).forEach { db.categoryDao().insertCategory(it) }
        listOf(
            Account(name = "الصندوق", categoryId = 2),
            Account(name = "المشتريات آجل", categoryId = 3),
            Account(name = "المبيعات آجل", categoryId = 4)
        ).forEach { db.accountDao().insertAccount(it) }
        db.itemGroupDao().insertItemGroup(ItemGroup(name = "عام"))
    }
}
