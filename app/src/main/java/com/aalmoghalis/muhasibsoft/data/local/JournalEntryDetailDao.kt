package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.JournalEntryDetail

@Dao
interface JournalEntryDetailDao {
    
    @Query("SELECT * FROM journal_entry_details WHERE entry_id = :entryId")
    fun getJournalEntryDetails(entryId: Long): kotlinx.coroutines.flow.Flow<List<JournalEntryDetail>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntryDetail(detail: JournalEntryDetail): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntryDetails(details: List<JournalEntryDetail>): List<Long>
    
    @Update
    suspend fun updateJournalEntryDetail(detail: JournalEntryDetail)
    
    @Query("DELETE FROM journal_entry_details WHERE entry_id = :entryId")
    suspend fun deleteJournalEntryDetails(entryId: Long)
}