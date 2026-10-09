package com.aalmoghalis.muhasibsoft.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.util.Date

// ==================== الكيانات الأساسية ====================

/**
 * نموذج الصنف/المنتج
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "barcode")
    val barcode: String? = null,
    
    @ColumnInfo(name = "group_id")
    val groupId: Long? = null,
    
    @ColumnInfo(name = "unit_id")
    val unitId: Long? = null,
    
    @ColumnInfo(name = "opening_quantity")
    val openingQuantity: Double = 0.0,
    
    @ColumnInfo(name = "current_quantity")
    val currentQuantity: Double = 0.0,
    
    @ColumnInfo(name = "unit_cost")
    val unitCost: Double = 0.0,
    
    @ColumnInfo(name = "selling_price")
    val sellingPrice: Double = 0.0,
    
    @ColumnInfo(name = "min_quantity")
    val minQuantity: Double = 0.0,
    
    @ColumnInfo(name = "expiry_date")
    val expiryDate: Long? = null,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * نموذج مجموعة الأصناف
 */
@Entity(tableName = "item_groups")
data class ItemGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)

/**
 * نموذج وحدة القياس
 */
@Entity(tableName = "units")
data class Unit(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "abbreviation")
    val abbreviation: String? = null
)

/**
 * نموذج المخزن
 */
@Entity(tableName = "warehouses")
data class Warehouse(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "phone")
    val phone: String? = null,
    
    @ColumnInfo(name = "address")
    val address: String? = null,
    
    @ColumnInfo(name = "is_main")
    val isMain: Boolean = false,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)

/**
 * نموذج العميل/المورد
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "type") // customer, supplier
    val type: String,
    
    @ColumnInfo(name = "phone")
    val phone: String? = null,
    
    @ColumnInfo(name = "email")
    val email: String? = null,
    
    @ColumnInfo(name = "address")
    val address: String? = null,
    
    @ColumnInfo(name = "tax_number")
    val taxNumber: String? = null,
    
    @ColumnInfo(name = "balance")
    val balance: Double = 0.0,
    
    @ColumnInfo(name = "credit_limit")
    val creditLimit: Double = 0.0,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)

/**
 * نموذج الحساب
 */
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    
    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null,
    
    @ColumnInfo(name = "code")
    val code: String? = null,
    
    @ColumnInfo(name = "balance")
    val balance: Double = 0.0,
    
    @ColumnInfo(name = "currency")
    val currency: String = "local",
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)

/**
 * نموذج التصنيف
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null,
    
    @ColumnInfo(name = "type") // income, expense, asset, liability
    val type: String,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)

/**
 * نموذج العملة
 */
@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "symbol")
    val symbol: String,
    
    @ColumnInfo(name = "code")
    val code: String,
    
    @ColumnInfo(name = "exchange_rate")
    val exchangeRate: Double = 1.0,
    
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false
)

/**
 * نموذج المبيعات
 */
@Entity(
    tableName = "sales",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["customer_id"]
        ),
        ForeignKey(
            entity = Warehouse::class,
            parentColumns = ["id"],
            childColumns = ["warehouse_id"]
        )
    ]
)
data class Sale(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String,
    
    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "customer_id")
    val customerId: Long? = null,
    
    @ColumnInfo(name = "warehouse_id")
    val warehouseId: Long? = null,
    
    @ColumnInfo(name = "type") // cash, credit
    val type: String = "cash",
    
    @ColumnInfo(name = "subtotal")
    val subtotal: Double = 0.0,
    
    @ColumnInfo(name = "discount")
    val discount: Double = 0.0,
    
    @ColumnInfo(name = "tax")
    val tax: Double = 0.0,
    
    @ColumnInfo(name = "shipping")
    val shipping: Double = 0.0,
    
    @ColumnInfo(name = "total")
    val total: Double = 0.0,
    
    @ColumnInfo(name = "paid")
    val paid: Double = 0.0,
    
    @ColumnInfo(name = "remaining")
    val remaining: Double = 0.0,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "created_by")
    val createdBy: Long? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * نموذج تفاصيل المبيعات
 */
@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = Sale::class,
            parentColumns = ["id"],
            childColumns = ["sale_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"]
        )
    ]
)
data class SaleItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "sale_id")
    val saleId: Long,
    
    @ColumnInfo(name = "item_id")
    val itemId: Long,
    
    @ColumnInfo(name = "quantity")
    val quantity: Double,
    
    @ColumnInfo(name = "price")
    val price: Double,
    
    @ColumnInfo(name = "discount")
    val discount: Double = 0.0,
    
    @ColumnInfo(name = "tax")
    val tax: Double = 0.0,
    
    @ColumnInfo(name = "total")
    val total: Double = 0.0
)

/**
 * نموذج المشتريات
 */
@Entity(
    tableName = "purchases",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["supplier_id"]
        ),
        ForeignKey(
            entity = Warehouse::class,
            parentColumns = ["id"],
            childColumns = ["warehouse_id"]
        )
    ]
)
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "invoice_number")
    val invoiceNumber: String,
    
    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "supplier_id")
    val supplierId: Long? = null,
    
    @ColumnInfo(name = "warehouse_id")
    val warehouseId: Long? = null,
    
    @ColumnInfo(name = "type") // cash, credit
    val type: String = "cash",
    
    @ColumnInfo(name = "subtotal")
    val subtotal: Double = 0.0,
    
    @ColumnInfo(name = "discount")
    val discount: Double = 0.0,
    
    @ColumnInfo(name = "tax")
    val tax: Double = 0.0,
    
    @ColumnInfo(name = "shipping")
    val shipping: Double = 0.0,
    
    @ColumnInfo(name = "total")
    val total: Double = 0.0,
    
    @ColumnInfo(name = "paid")
    val paid: Double = 0.0,
    
    @ColumnInfo(name = "remaining")
    val remaining: Double = 0.0,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "created_by")
    val createdBy: Long? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * نموذج تفاصيل المشتريات
 */
@Entity(
    tableName = "purchase_items",
    foreignKeys = [
        ForeignKey(
            entity = Purchase::class,
            parentColumns = ["id"],
            childColumns = ["purchase_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"]
        )
    ]
)
data class PurchaseItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "purchase_id")
    val purchaseId: Long,
    
    @ColumnInfo(name = "item_id")
    val itemId: Long,
    
    @ColumnInfo(name = "quantity")
    val quantity: Double,
    
    @ColumnInfo(name = "cost")
    val cost: Double,
    
    @ColumnInfo(name = "total")
    val total: Double = 0.0
)

/**
 * نموذج القيود اليومية
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "reference_number")
    val referenceNumber: String,
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "currency")
    val currency: String = "local",
    
    @ColumnInfo(name = "created_by")
    val createdBy: Long? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * نموذج تفاصيل القيود
 */
@Entity(
    tableName = "journal_entry_details",
    foreignKeys = [
        ForeignKey(
            entity = JournalEntry::class,
            parentColumns = ["id"],
            childColumns = ["entry_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["account_id"]
        )
    ]
)
data class JournalEntryDetail(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "entry_id")
    val entryId: Long,
    
    @ColumnInfo(name = "account_id")
    val accountId: Long,
    
    @ColumnInfo(name = "debit")
    val debit: Double = 0.0,
    
    @ColumnInfo(name = "credit")
    val credit: Double = 0.0,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null
)

/**
 * نموذج سندات القبض والصرف
 */
@Entity(tableName = "voucher")
data class Voucher(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "type") // receipt, payment
    val type: String,
    
    @ColumnInfo(name = "number")
    val number: String,
    
    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "account_id")
    val accountId: Long? = null,
    
    @ColumnInfo(name = "amount")
    val amount: Double,
    
    @ColumnInfo(name = "currency")
    val currency: String = "local",
    
    @ColumnInfo(name = "description")
    val description: String? = null,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "created_by")
    val createdBy: Long? = null
)

/**
 * نموذج العمليات المخزنية
 */
@Entity(tableName = "inventory_operations")
data class InventoryOperation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "type") // dispense, supply, transfer, adjustment
    val type: String,
    
    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "from_warehouse_id")
    val fromWarehouseId: Long? = null,
    
    @ColumnInfo(name = "to_warehouse_id")
    val toWarehouseId: Long? = null,
    
    @ColumnInfo(name = "account_id")
    val accountId: Long? = null,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "created_by")
    val createdBy: Long? = null
)

/**
 * نموذج تفاصيل العمليات المخزنية
 */
@Entity(
    tableName = "inventory_operation_items",
    foreignKeys = [
        ForeignKey(
            entity = InventoryOperation::class,
            parentColumns = ["id"],
            childColumns = ["operation_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"]
        )
    ]
)
data class InventoryOperationItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "operation_id")
    val operationId: Long,
    
    @ColumnInfo(name = "item_id")
    val itemId: Long,
    
    @ColumnInfo(name = "quantity")
    val quantity: Double,
    
    @ColumnInfo(name = "cost")
    val cost: Double = 0.0,
    
    @ColumnInfo(name = "adjustment_type") // increase, decrease
    val adjustmentType: String? = null
)

/**
 * نموذج المستخدم
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "password")
    val password: String,
    
    @ColumnInfo(name = "role") // admin, user
    val role: String = "user",
    
    @ColumnInfo(name = "warehouse_id")
    val warehouseId: Long? = null,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * نموذج الإشعارات
 */
@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "message")
    val message: String,
    
    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,
    
    @ColumnInfo(name = "type")
    val type: String? = null
)

// ==================== الكائنات المساعدة ====================

/**
 * كائن لبيانات البيع الكاملة
 */
data class SaleWithItems(
    val sale: Sale,
    val items: List<SaleItem>,
    val customer: Contact? = null,
    val warehouse: Warehouse? = null
)

/**
 * كائن لبيانات الشراء الكاملة
 */
data class PurchaseWithItems(
    val purchase: Purchase,
    val items: List<PurchaseItem>,
    val supplier: Contact? = null,
    val warehouse: Warehouse? = null
)

/**
 * كائن لرصيد الصنف
 */
data class ItemBalance(
    val itemId: Long,
    val itemName: String,
    val warehouseId: Long,
    val warehouseName: String,
    val quantity: Double,
    val unitCost: Double,
    val totalValue: Double
)

/**
 * كائن للحركة المخزنية
 */
data class InventoryMovement(
    val id: Long,
    val date: Long,
    val type: String,
    val itemId: Long,
    val itemName: String,
    val quantity: Double,
    val cost: Double,
    val warehouseId: Long,
    val warehouseName: String,
    val reference: String?
)

/**
 * كائن لإعدادات التطبيق
 */
data class AppSettings(
    val companyName: String = "",
    val address: String = "",
    val phone: String = "",
    val taxNumber: String = "",
    val currency: String = "local",
    val taxRate: Double = 0.0,
    val invoicePrefix: String = "",
    val invoiceNumber: Int = 1,
    val enableNotifications: Boolean = true,
    val autoBackup: Boolean = false,
    val thermalPrinter: Boolean = false,
    val barcodeEnabled: Boolean = false
)

/**
 * أنواع العمليات
 */
enum class OperationType {
    SALE,
    PURCHASE,
    INVENTORY_DISPENSE,
    INVENTORY_SUPPLY,
    INVENTORY_TRANSFER,
    INVENTORY_ADJUSTMENT,
    JOURNAL_ENTRY,
    RECEIPT,
    PAYMENT
}

/**
 * أنواع العملات
 */
enum class CurrencyType {
    LOCAL,
    USD
}

/**
 * أنواع السندات
 */
enum class VoucherType {
    RECEIPT,
    PAYMENT
}

/**
 * حالة العملية
 */
enum class OperationStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}
