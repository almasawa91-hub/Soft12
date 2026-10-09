package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.InventoryOperationItem

@Dao
interface InventoryOperationItemDao {
    
    @Query("SELECT * FROM inventory_operation_items WHERE operation_id = :operationId")
    fun getInventoryOperationItems(operationId: Long): kotlinx.coroutines.flow.Flow<List<InventoryOperationItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryOperationItem(item: InventoryOperationItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryOperationItems(items: List<InventoryOperationItem>): List<Long>
    
    @Update
    suspend fun updateInventoryOperationItem(item: InventoryOperationItem)
    
    @Query("DELETE FROM inventory_operation_items WHERE operation_id = :operationId")
    suspend fun deleteInventoryOperationItems(operationId: Long)
}