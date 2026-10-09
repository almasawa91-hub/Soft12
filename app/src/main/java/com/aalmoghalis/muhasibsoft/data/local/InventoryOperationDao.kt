package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.InventoryOperation
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryOperationDao {
    
    @Query("SELECT * FROM inventory_operations ORDER BY date DESC")
    fun getAllInventoryOperations(): Flow<List<InventoryOperation>>
    
    @Query("SELECT * FROM inventory_operations WHERE id = :id")
    suspend fun getInventoryOperationById(id: Long): InventoryOperation?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryOperation(operation: InventoryOperation): Long
    
    @Update
    suspend fun updateInventoryOperation(operation: InventoryOperation)
    
    @Query("DELETE FROM inventory_operations WHERE id = :id")
    suspend fun deleteInventoryOperation(id: Long)
}