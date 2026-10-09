package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    
    @Query("SELECT * FROM accounts WHERE is_active = 1 ORDER BY name ASC")
    fun getAllAccounts(): Flow<List<Account>>
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: Long): Account?
    
    @Query("SELECT * FROM accounts WHERE category_id = :categoryId")
    fun getAccountsByCategory(categoryId: Long): Flow<List<Account>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account): Long
    
    @Update
    suspend fun updateAccount(account: Account)
    
    @Query("UPDATE accounts SET balance = balance + :amount WHERE id = :accountId")
    suspend fun updateAccountBalance(accountId: Long, amount: Double)
    
    @Query("UPDATE accounts SET is_active = 0 WHERE id = :accountId")
    suspend fun deleteAccount(accountId: Long)
}