package com.aalmoghalis.muhasibsoft.di

import android.content.Context
import androidx.room.Room
import com.aalmoghalis.muhasibsoft.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "muhasib_soft.db"
        ).fallbackToDestructiveMigration()
         .build()
    }
    
    @Provides
    fun provideItemDao(db: AppDatabase): ItemDao = db.itemDao()
    
    @Provides
    fun provideSaleDao(db: AppDatabase): SaleDao = db.saleDao()
    
    @Provides
    fun provideSaleItemDao(db: AppDatabase): SaleItemDao = db.saleItemDao()
    
    @Provides
    fun providePurchaseDao(db: AppDatabase): PurchaseDao = db.purchaseDao()
    
    @Provides
    fun providePurchaseItemDao(db: AppDatabase): PurchaseItemDao = db.purchaseItemDao()
    
    @Provides
    fun provideContactDao(db: AppDatabase): ContactDao = db.contactDao()
    
    @Provides
    fun provideWarehouseDao(db: AppDatabase): WarehouseDao = db.warehouseDao()
    
    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
    
    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    
    @Provides
    fun provideCurrencyDao(db: AppDatabase): CurrencyDao = db.currencyDao()
    
    @Provides
    fun provideJournalEntryDao(db: AppDatabase): JournalEntryDao = db.journalEntryDao()
    
    @Provides
    fun provideJournalEntryDetailDao(db: AppDatabase): JournalEntryDetailDao = db.journalEntryDetailDao()
    
    @Provides
    fun provideVoucherDao(db: AppDatabase): VoucherDao = db.voucherDao()
    
    @Provides
    fun provideInventoryOperationDao(db: AppDatabase): InventoryOperationDao = db.inventoryOperationDao()
    
    @Provides
    fun provideInventoryOperationItemDao(db: AppDatabase): InventoryOperationItemDao = db.inventoryOperationItemDao()
    
    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    
    @Provides
    fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()
}