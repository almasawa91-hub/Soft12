package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Warehouse
import kotlinx.coroutines.flow.Flow

@Dao
interface WarehouseDao {
    
    @Query("SELECT * FROM warehouses WHERE is_active = 1 ORDER BY name ASC")
    fun getAllWarehouses(): Flow<List<Warehouse>>
    
    @Query("SELECT * FROM warehouses WHERE id = :id")
    suspend fun getWarehouseById(id: Long): Warehouse?
    
    @Query("SELECT * FROM warehouses WHERE is_main = 1 LIMIT 1")
    suspend fun getMainWarehouse(): Warehouse?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarehouse(warehouse: Warehouse): Long
    
    @Update
    suspend fun updateWarehouse(warehouse: Warehouse)
    
    @Query("UPDATE warehouses SET is_active = 0 WHERE id = :warehouseId")
    suspend fun deleteWarehouse(warehouseId: Long)
}