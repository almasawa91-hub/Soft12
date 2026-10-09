package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    
    @Query("SELECT * FROM currencies")
    fun getAllCurrencies(): Flow<List<Currency>>
    
    @Query("SELECT * FROM currencies WHERE id = :id")
    suspend fun getCurrencyById(id: Long): Currency?
    
    @Query("SELECT * FROM currencies WHERE is_default = 1 LIMIT 1")
    suspend fun getDefaultCurrency(): Currency?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: Currency): Long
    
    @Update
    suspend fun updateCurrency(currency: Currency)
    
    @Query("UPDATE currencies SET exchange_rate = :rate WHERE id = :currencyId")
    suspend fun updateExchangeRate(currencyId: Long, rate: Double)
}