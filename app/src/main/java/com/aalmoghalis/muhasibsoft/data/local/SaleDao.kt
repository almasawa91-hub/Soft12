package com.aalmoghalis.muhasibsoft.data.local
import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Sale
import kotlinx.coroutines.flow.Flow
@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<Sale>>
    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1")
    suspend fun getSaleById(id: Long): Sale?
    @Query("SELECT * FROM sales WHERE customer_id = :customerId ORDER BY date DESC")
    fun getSalesByCustomer(customerId: Long): Flow<List<Sale>>
    @Query("SELECT * FROM sales WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getSalesByDateRange(startDate: Long, endDate: Long): Flow<List<Sale>>
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSale(sale: Sale): Long
    @Update suspend fun updateSale(sale: Sale)
    @Query("SELECT COUNT(*) FROM sales")
    suspend fun getSaleCount(): Int
    @Query("SELECT COALESCE(SUM(total), 0) FROM sales WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalSales(startDate: Long, endDate: Long): Double
    @Query("SELECT COALESCE(SUM(remaining), 0) FROM sales WHERE remaining > 0")
    suspend fun getTotalReceivables(): Double
    @Query("SELECT invoice_number FROM sales ORDER BY id DESC LIMIT 1")
    suspend fun getLastInvoiceNumber(): String?
}
