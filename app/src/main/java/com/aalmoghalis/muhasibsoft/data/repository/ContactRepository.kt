package com.aalmoghalis.muhasibsoft.data.repository

import com.aalmoghalis.muhasibsoft.data.local.ContactDao
import com.aalmoghalis.muhasibsoft.data.model.Contact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    private val contactDao: ContactDao
) {
    
    fun getContactsByType(type: String): Flow<List<Contact>> = 
        contactDao.getContactsByType(type)
    
    suspend fun getContactById(id: Long): Contact? = contactDao.getContactById(id)
    
    fun searchContacts(query: String): Flow<List<Contact>> = 
        contactDao.searchContacts(query)
    
    suspend fun insertContact(contact: Contact): Long = contactDao.insertContact(contact)
    
    suspend fun updateContact(contact: Contact) = contactDao.updateContact(contact)
    
    suspend fun updateContactBalance(contactId: Long, amount: Double) = 
        contactDao.updateContactBalance(contactId, amount)
    
    suspend fun deleteContact(contactId: Long) = contactDao.deleteContact(contactId)
}