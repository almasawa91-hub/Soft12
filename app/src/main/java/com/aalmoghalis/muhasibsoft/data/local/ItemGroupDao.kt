package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.ItemGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemGroupDao {
    
    @Query("SELECT * FROM item_groups WHERE is_active = 1 ORDER BY name ASC")
    fun getAllItemGroups(): Flow<List<ItemGroup>>
    
    @Query("SELECT * FROM item_groups WHERE id = :id")
    suspend fun getItemGroupById(id: Long): ItemGroup?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemGroup(group: ItemGroup): Long
    
    @Update
    suspend fun updateItemGroup(group: ItemGroup)
    
    @Query("UPDATE item_groups SET is_active = 0 WHERE id = :groupId")
    suspend fun deleteItemGroup(groupId: Long)
}