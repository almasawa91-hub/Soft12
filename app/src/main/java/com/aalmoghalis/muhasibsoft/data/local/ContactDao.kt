package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    
    @Query("SELECT * FROM contacts WHERE type = :type AND is_active = 1 ORDER BY name ASC")
    fun getContactsByType(type: String): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): Contact?
    
    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :searchQuery || '%' AND is_active = 1")
    fun searchContacts(searchQuery: String): Flow<List<Contact>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long
    
    @Update
    suspend fun updateContact(contact: Contact)
    
    @Query("UPDATE contacts SET balance = balance + :amount WHERE id = :contactId")
    suspend fun updateContactBalance(contactId: Long, amount: Double)
    
    @Query("UPDATE contacts SET is_active = 0 WHERE id = :contactId")
    suspend fun deleteContact(contactId: Long)
}