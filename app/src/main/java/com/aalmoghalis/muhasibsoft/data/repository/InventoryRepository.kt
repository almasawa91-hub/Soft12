package com.aalmoghalis.muhasibsoft.data.repository

import com.aalmoghalis.muhasibsoft.data.local.InventoryOperationDao
import com.aalmoghalis.muhasibsoft.data.local.InventoryOperationItemDao
import com.aalmoghalis.muhasibsoft.data.model.InventoryOperation
import com.aalmoghalis.muhasibsoft.data.model.InventoryOperationItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val operationDao: InventoryOperationDao,
    private val operationItemDao: InventoryOperationItemDao
) {

    fun getAllOperations(): Flow<List<InventoryOperation>> =
        operationDao.getAllInventoryOperations()

    suspend fun getOperationById(id: Long): InventoryOperation? =
        operationDao.getInventoryOperationById(id)

    fun getOperationItems(operationId: Long): Flow<List<InventoryOperationItem>> =
        operationItemDao.getInventoryOperationItems(operationId)

    suspend fun insertOperation(
        operation: InventoryOperation,
        items: List<InventoryOperationItem>
    ): Long {
        val operationId = operationDao.insertInventoryOperation(operation)
        val opItems = items.map { it.copy(operationId = operationId) }
        operationItemDao.insertInventoryOperationItems(opItems)
        return operationId
    }

    suspend fun deleteOperation(id: Long) {
        operationItemDao.deleteInventoryOperationItems(id)
        operationDao.deleteInventoryOperation(id)
    }
}