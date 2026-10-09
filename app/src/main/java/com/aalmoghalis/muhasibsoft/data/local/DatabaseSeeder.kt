package com.aalmoghalis.muhasibsoft.data.local

import android.content.Context
import com.aalmoghalis.muhasibsoft.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * تهيئة قاعدة البيانات بالبيانات الأولية عند أول تشغيل
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val db: AppDatabase
) {

    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        // التحقق من وجود بيانات
        val userCount = db.userDao().getAllUsers().let { flow ->
            var count = 0
            flow.collect { count = it.size }
            count
        }

        if (userCount > 0) return@withContext

        // 1. إنشاء المستخدم الإداري
        db.userDao().insertUser(
            User(
                name = "مدير النظام",
                username = "admin",
                password = "1234",
                role = "admin"
            )
        )

        // 2. إنشاء المخزن الرئيسي
        db.warehouseDao().insertWarehouse(
            Warehouse(
                name = "المخزن الرئيسي",
                isMain = true
            )
        )

        // 3. إنشاء العملات
        db.currencyDao().insertCurrency(
            Currency(
                name = "محلي",
                symbol = "ر.ي",
                code = "YR",
                exchangeRate = 1.0,
                isDefault = true
            )
        )
        db.currencyDao().insertCurrency(
            Currency(
                name = "دولار",
                symbol = "$",
                code = "USD",
                exchangeRate = 500.0
            )
        )

        // 4. إنشاء وحدات القياس
        val units = listOf(
            Unit(name = "حبة", abbreviation = "حبة"),
            Unit(name = "كيلو", abbreviation = "كيلو"),
            Unit(name = "كرتون", abbreviation = "كرتون"),
            Unit(name = "كيس", abbreviation = "كيس")
        )
        units.forEach { db.unitDao().insertUnit(it) }

        // 5. إنشاء التصنيفات الأساسية
        val categories = listOf(
            Category(name = "عام", type = "general"),
            Category(name = "الصندوق", type = "asset"),
            Category(name = "المشتريات", type = "expense"),
            Category(name = "المبيعات", type = "income"),
            Category(name = "الموردون", type = "liability"),
            Category(name = "العملاء", type = "asset")
        )
        categories.forEach { db.categoryDao().insertCategory(it) }

        // 6. إنشاء الحسابات الأساسية
        val accounts = listOf(
            Account(name = "الصندوق", categoryId = 2),
            Account(name = "المشتريات آجل", categoryId = 3),
            Account(name = "المبيعات آجل", categoryId = 4)
        )
        accounts.forEach { db.accountDao().insertAccount(it) }

        // 7. إنشاء مجموعات الأصناف
        db.itemGroupDao().insertItemGroup(
            ItemGroup(name = "عام")
        )
    }
}