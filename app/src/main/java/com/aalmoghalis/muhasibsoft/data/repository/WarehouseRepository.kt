package com.aalmoghalis.muhasibsoft.data.repository

import com.aalmoghalis.muhasibsoft.data.local.WarehouseDao
import com.aalmoghalis.muhasibsoft.data.model.Warehouse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WarehouseRepository @Inject constructor(
    private val warehouseDao: WarehouseDao
) {
    
    fun getAllWarehouses(): Flow<List<Warehouse>> = warehouseDao.getAllWarehouses()
    
    suspend fun getWarehouseById(id: Long): Warehouse? = warehouseDao.getWarehouseById(id)
    
    suspend fun getMainWarehouse(): Warehouse? = warehouseDao.getMainWarehouse()
    
    suspend fun insertWarehouse(warehouse: Warehouse): Long = warehouseDao.insertWarehouse(warehouse)
    
    suspend fun updateWarehouse(warehouse: Warehouse) = warehouseDao.updateWarehouse(warehouse)
    
    suspend fun deleteWarehouse(warehouseId: Long) = warehouseDao.deleteWarehouse(warehouseId)
}