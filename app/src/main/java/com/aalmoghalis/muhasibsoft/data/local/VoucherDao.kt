package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Voucher
import kotlinx.coroutines.flow.Flow

@Dao
interface VoucherDao {
    
    @Query("SELECT * FROM voucher ORDER BY date DESC")
    fun getAllVouchers(): Flow<List<Voucher>>
    
    @Query("SELECT * FROM voucher WHERE id = :id")
    suspend fun getVoucherById(id: Long): Voucher?
    
    @Query("SELECT * FROM voucher WHERE type = :type ORDER BY date DESC")
    fun getVouchersByType(type: String): Flow<List<Voucher>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: Voucher): Long
    
    @Update
    suspend fun updateVoucher(voucher: Voucher)
    
    @Query("DELETE FROM voucher WHERE id = :id")
    suspend fun deleteVoucher(id: Long)
}