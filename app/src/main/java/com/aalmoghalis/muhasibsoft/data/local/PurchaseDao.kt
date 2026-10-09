package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Purchase
import com.aalmoghalis.muhasibsoft.data.model.PurchaseWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    
    @Transaction
    @Query("SELECT * FROM purchases ORDER BY date DESC")
    fun getAllPurchases(): Flow<List<PurchaseWithItems>>
    
    @Transaction
    @Query("SELECT * FROM purchases WHERE id = :id")
    suspend fun getPurchaseById(id: Long): PurchaseWithItems?
    
    @Transaction
    @Query("SELECT * FROM purchases WHERE supplier_id = :supplierId ORDER BY date DESC")
    fun getPurchasesBySupplier(supplierId: Long): Flow<List<PurchaseWithItems>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: Purchase): Long
    
    @Update
    suspend fun updatePurchase(purchase: Purchase)
    
    @Query("DELETE FROM purchases WHERE id = :id")
    suspend fun deletePurchase(id: Long)
    
    @Query("SELECT SUM(total) FROM purchases WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalPurchases(startDate: Long, endDate: Long): Double?
    
    @Query("SELECT invoice_number FROM purchases ORDER BY id DESC LIMIT 1")
    suspend fun getLastInvoiceNumber(): String?
}