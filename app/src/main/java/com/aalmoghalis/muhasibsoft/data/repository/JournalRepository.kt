package com.aalmoghalis.muhasibsoft.data.repository

import com.aalmoghalis.muhasibsoft.data.local.JournalEntryDao
import com.aalmoghalis.muhasibsoft.data.local.JournalEntryDetailDao
import com.aalmoghalis.muhasibsoft.data.model.JournalEntry
import com.aalmoghalis.muhasibsoft.data.model.JournalEntryDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepository @Inject constructor(
    private val journalEntryDao: JournalEntryDao,
    private val journalEntryDetailDao: JournalEntryDetailDao
) {

    fun getAllJournalEntries(): Flow<List<JournalEntry>> =
        journalEntryDao.getAllJournalEntries()

    suspend fun getJournalEntryById(id: Long): JournalEntry? =
        journalEntryDao.getJournalEntryById(id)

    fun getJournalEntryDetails(entryId: Long): Flow<List<JournalEntryDetail>> =
        journalEntryDetailDao.getJournalEntryDetails(entryId)

    suspend fun insertJournalEntry(entry: JournalEntry, details: List<JournalEntryDetail>): Long {
        val entryId = journalEntryDao.insertJournalEntry(entry)
        val entryDetails = details.map { it.copy(entryId = entryId) }
        journalEntryDetailDao.insertJournalEntryDetails(entryDetails)
        return entryId
    }

    suspend fun deleteJournalEntry(id: Long) {
        journalEntryDetailDao.deleteJournalEntryDetails(id)
        journalEntryDao.deleteJournalEntry(id)
    }
}