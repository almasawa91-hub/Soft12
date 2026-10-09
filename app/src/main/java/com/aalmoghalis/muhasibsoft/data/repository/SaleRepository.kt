package com.aalmoghalis.muhasibsoft.data.repository

import com.aalmoghalis.muhasibsoft.data.local.ItemDao
import com.aalmoghalis.muhasibsoft.data.local.SaleDao
import com.aalmoghalis.muhasibsoft.data.local.SaleItemDao
import com.aalmoghalis.muhasibsoft.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaleRepository @Inject constructor(
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao,
    private val itemDao: ItemDao
) {
    
    fun getAllSales(): Flow<List<SaleWithItems>> = saleDao.getAllSales()
    
    suspend fun getSaleById(id: Long): SaleWithItems? = saleDao.getSaleById(id)
    
    fun getSalesByCustomer(customerId: Long): Flow<List<SaleWithItems>> = 
        saleDao.getSalesByCustomer(customerId)
    
    suspend fun insertSale(sale: Sale, items: List<SaleItem>): Long {
        val saleId = saleDao.insertSale(sale)
        val saleItems = items.map { it.copy(saleId = saleId) }
        saleItemDao.insertSaleItems(saleItems)
        
        // تحديث المخزون
        items.forEach { saleItem ->
            itemDao.updateItemQuantity(saleItem.itemId, -saleItem.quantity)
        }
        
        return saleId
    }
    
    suspend fun updateSale(sale: Sale) = saleDao.updateSale(sale)
    
    suspend fun deleteSale(id: Long) {
        val sale = saleDao.getSaleById(id)
        sale?.items?.forEach { saleItem ->
            itemDao.updateItemQuantity(saleItem.itemId, saleItem.quantity)
        }
        saleDao.deleteSale(id)
    }
    
    suspend fun getSaleCount(): Int = saleDao.getSaleCount()
    
    suspend fun getTotalSales(startDate: Long, endDate: Long): Double? = 
        saleDao.getTotalSales(startDate, endDate)
    
    suspend fun getTotalReceivables(): Double? = saleDao.getTotalReceivables()
    
    suspend fun generateInvoiceNumber(): String {
        val lastNumber = saleDao.getLastInvoiceNumber()
        val nextNumber = if (lastNumber != null) {
            lastNumber.toIntOrNull()?.plus(1) ?: 1
        } else {
            1
        }
        return "#$nextNumber"
    }
    
    fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<Sale>> = 
        saleDao.getSalesByDateRange(startDate, endDate)
}