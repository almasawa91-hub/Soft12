package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Sale
import com.aalmoghalis.muhasibsoft.data.model.SaleWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    
    @Transaction
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<SaleWithItems>>
    
    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleById(id: Long): SaleWithItems?
    
    @Transaction
    @Query("SELECT * FROM sales WHERE customer_id = :customerId ORDER BY date DESC")
    fun getSalesByCustomer(customerId: Long): Flow<List<SaleWithItems>>
    
    @Query("SELECT * FROM sales WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<Sale>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long
    
    @Update
    suspend fun updateSale(sale: Sale)
    
    @Query("DELETE FROM sales WHERE id = :id")
    suspend fun deleteSale(id: Long)
    
    @Query("SELECT COUNT(*) FROM sales")
    suspend fun getSaleCount(): Int
    
    @Query("SELECT SUM(total) FROM sales WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalSales(startDate: Long, endDate: Long): Double?
    
    @Query("SELECT SUM(remaining) FROM sales WHERE remaining > 0")
    suspend fun getTotalReceivables(): Double?
    
    @Query("SELECT invoice_number FROM sales ORDER BY id DESC LIMIT 1")
    suspend fun getLastInvoiceNumber(): String?
}