package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.PurchaseItem

@Dao
interface PurchaseItemDao {
    
    @Query("SELECT * FROM purchase_items WHERE purchase_id = :purchaseId")
    fun getPurchaseItems(purchaseId: Long): kotlinx.coroutines.flow.Flow<List<PurchaseItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseItem(item: PurchaseItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseItems(items: List<PurchaseItem>): List<Long>
    
    @Update
    suspend fun updatePurchaseItem(item: PurchaseItem)
    
    @Query("DELETE FROM purchase_items WHERE purchase_id = :purchaseId")
    suspend fun deletePurchaseItems(purchaseId: Long)
}