package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Unit
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    
    @Query("SELECT * FROM units ORDER BY name ASC")
    fun getAllUnits(): Flow<List<Unit>>
    
    @Query("SELECT * FROM units WHERE id = :id")
    suspend fun getUnitById(id: Long): Unit?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: Unit): Long
    
    @Update
    suspend fun updateUnit(unit: Unit)
    
    @Query("DELETE FROM units WHERE id = :unitId")
    suspend fun deleteUnit(unitId: Long)
}