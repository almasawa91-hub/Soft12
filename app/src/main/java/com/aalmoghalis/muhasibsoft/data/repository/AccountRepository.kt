package com.aalmoghalis.muhasibsoft.data.repository

import com.aalmoghalis.muhasibsoft.data.local.AccountDao
import com.aalmoghalis.muhasibsoft.data.model.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountDao: AccountDao
) {
    
    fun getAllAccounts(): Flow<List<Account>> = accountDao.getAllAccounts()
    
    suspend fun getAccountById(id: Long): Account? = accountDao.getAccountById(id)
    
    fun getAccountsByCategory(categoryId: Long): Flow<List<Account>> = 
        accountDao.getAccountsByCategory(categoryId)
    
    suspend fun insertAccount(account: Account): Long = accountDao.insertAccount(account)
    
    suspend fun updateAccount(account: Account) = accountDao.updateAccount(account)
    
    suspend fun updateAccountBalance(accountId: Long, amount: Double) = 
        accountDao.updateAccountBalance(accountId, amount)
    
    suspend fun deleteAccount(accountId: Long) = accountDao.deleteAccount(accountId)
}