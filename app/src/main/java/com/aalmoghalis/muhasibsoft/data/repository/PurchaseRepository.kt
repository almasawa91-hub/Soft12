package com.aalmoghalis.muhasibsoft.data.repository

import com.aalmoghalis.muhasibsoft.data.local.ItemDao
import com.aalmoghalis.muhasibsoft.data.local.PurchaseDao
import com.aalmoghalis.muhasibsoft.data.local.PurchaseItemDao
import com.aalmoghalis.muhasibsoft.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PurchaseRepository @Inject constructor(
    private val purchaseDao: PurchaseDao,
    private val purchaseItemDao: PurchaseItemDao,
    private val itemDao: ItemDao
) {
    
    fun getAllPurchases(): Flow<List<PurchaseWithItems>> = purchaseDao.getAllPurchases()
    
    suspend fun getPurchaseById(id: Long): PurchaseWithItems? = purchaseDao.getPurchaseById(id)
    
    suspend fun insertPurchase(purchase: Purchase, items: List<PurchaseItem>): Long {
        val purchaseId = purchaseDao.insertPurchase(purchase)
        val purchaseItems = items.map { it.copy(purchaseId = purchaseId) }
        purchaseItemDao.insertPurchaseItems(purchaseItems)
        
        // تحديث المخزون
        items.forEach { purchaseItem ->
            itemDao.updateItemQuantity(purchaseItem.itemId, purchaseItem.quantity)
        }
        
        return purchaseId
    }
    
    suspend fun updatePurchase(purchase: Purchase) = purchaseDao.updatePurchase(purchase)
    
    suspend fun deletePurchase(id: Long) {
        val purchase = purchaseDao.getPurchaseById(id)
        purchase?.items?.forEach { purchaseItem ->
            itemDao.updateItemQuantity(purchaseItem.itemId, -purchaseItem.quantity)
        }
        purchaseDao.deletePurchase(id)
    }
    
    suspend fun getTotalPurchases(startDate: Long, endDate: Long): Double? = 
        purchaseDao.getTotalPurchases(startDate, endDate)
    
    suspend fun generateInvoiceNumber(): String {
        val lastNumber = purchaseDao.getLastInvoiceNumber()
        val nextNumber = if (lastNumber != null) {
            lastNumber.toIntOrNull()?.plus(1) ?: 1
        } else {
            1
        }
        return "#$nextNumber"
    }
}