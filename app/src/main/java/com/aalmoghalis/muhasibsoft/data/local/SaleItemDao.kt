package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.SaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleItemDao {
    
    @Query("SELECT * FROM sale_items WHERE sale_id = :saleId")
    fun getSaleItems(saleId: Long): Flow<List<SaleItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItem(saleItem: SaleItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(items: List<SaleItem>): List<Long>
    
    @Update
    suspend fun updateSaleItem(saleItem: SaleItem)
    
    @Query("DELETE FROM sale_items WHERE sale_id = :saleId")
    suspend fun deleteSaleItems(saleId: Long)
    
    @Query("DELETE FROM sale_items WHERE id = :id")
    suspend fun deleteSaleItem(id: Long)
    
    @Query("SELECT SUM(total) FROM sale_items WHERE sale_id = :saleId")
    suspend fun getSaleTotal(saleId: Long): Double?
}