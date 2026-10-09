package com.aalmoghalis.muhasibsoft.data.local

import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    
    @Query("SELECT * FROM items WHERE is_active = 1 ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>
    
    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): Item?
    
    @Query("SELECT * FROM items WHERE name LIKE '%' || :searchQuery || '%' AND is_active = 1")
    fun searchItems(searchQuery: String): Flow<List<Item>>
    
    @Query("SELECT * FROM items WHERE group_id = :groupId AND is_active = 1")
    fun getItemsByGroup(groupId: Long): Flow<List<Item>>
    
    @Query("SELECT * FROM items WHERE barcode = :barcode")
    suspend fun getItemByBarcode(barcode: String): Item?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<Item>): List<Long>
    
    @Update
    suspend fun updateItem(item: Item)
    
    @Query("UPDATE items SET current_quantity = current_quantity + :quantity WHERE id = :itemId")
    suspend fun updateItemQuantity(itemId: Long, quantity: Double)
    
    @Query("UPDATE items SET selling_price = :price WHERE id = :itemId")
    suspend fun updateItemPrice(itemId: Long, price: Double)
    
    @Query("UPDATE items SET is_active = 0 WHERE id = :itemId")
    suspend fun deleteItem(itemId: Long)
    
    @Query("DELETE FROM items WHERE id = :itemId")
    suspend fun hardDeleteItem(itemId: Long)
    
    @Query("SELECT COUNT(*) FROM items WHERE is_active = 1")
    suspend fun getItemCount(): Int
    
    @Query("SELECT * FROM items WHERE current_quantity <= min_quantity AND is_active = 1")
    fun getLowStockItems(): Flow<List<Item>>
    
    @Query("SELECT SUM(current_quantity * unit_cost) FROM items WHERE is_active = 1")
    suspend fun getTotalInventoryValue(): Double?
}