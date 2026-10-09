package com.aalmoghalis.muhasibsoft.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aalmoghalis.muhasibsoft.data.model.*
@Database(
    entities = [Item::class, ItemGroup::class, Unit::class, Warehouse::class, Contact::class,
        Account::class, Category::class, Currency::class, Sale::class, SaleItem::class,
        Purchase::class, PurchaseItem::class, JournalEntry::class, JournalEntryDetail::class,
        Voucher::class, InventoryOperation::class, InventoryOperationItem::class, User::class,
        Notification::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun saleDao(): SaleDao
}
