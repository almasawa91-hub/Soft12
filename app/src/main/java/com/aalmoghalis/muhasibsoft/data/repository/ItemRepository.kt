package com.aalmoghalis.muhasibsoft.data.repository

import com.aalmoghalis.muhasibsoft.data.local.ItemDao
import com.aalmoghalis.muhasibsoft.data.model.Item
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val itemDao: ItemDao
) {
    
    fun getAllItems(): Flow<List<Item>> = itemDao.getAllItems()
    
    suspend fun getItemById(id: Long): Item? = itemDao.getItemById(id)
    
    fun searchItems(query: String): Flow<List<Item>> = itemDao.searchItems(query)
    
    fun getItemsByGroup(groupId: Long): Flow<List<Item>> = itemDao.getItemsByGroup(groupId)
    
    suspend fun getItemByBarcode(barcode: String): Item? = itemDao.getItemByBarcode(barcode)
    
    suspend fun insertItem(item: Item): Long = itemDao.insertItem(item)
    
    suspend fun insertItems(items: List<Item>): List<Long> = itemDao.insertItems(items)
    
    suspend fun updateItem(item: Item) = itemDao.updateItem(item)
    
    suspend fun updateItemQuantity(itemId: Long, quantity: Double) = 
        itemDao.updateItemQuantity(itemId, quantity)
    
    suspend fun deleteItem(itemId: Long) = itemDao.deleteItem(itemId)
    
    suspend fun getItemCount(): Int = itemDao.getItemCount()
    
    fun getLowStockItems(): Flow<List<Item>> = itemDao.getLowStockItems()
    
    suspend fun getTotalInventoryValue(): Double? = itemDao.getTotalInventoryValue()
}