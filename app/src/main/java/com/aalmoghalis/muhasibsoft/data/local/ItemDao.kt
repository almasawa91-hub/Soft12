package com.aalmoghalis.muhasibsoft.data.local
import androidx.room.*
import com.aalmoghalis.muhasibsoft.data.model.Item
import kotlinx.coroutines.flow.Flow
@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE is_active = 1 ORDER BY name COLLATE NOCASE ASC")
    fun getAllItems(): Flow<List<Item>>
    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): Item?
    @Query("SELECT * FROM items WHERE is_active = 1 AND (name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%') ORDER BY name")
    fun searchItems(query: String): Flow<List<Item>>
    @Query("SELECT * FROM items WHERE group_id = :groupId AND is_active = 1 ORDER BY name")
    fun getItemsByGroup(groupId: Long): Flow<List<Item>>
    @Query("SELECT * FROM items WHERE barcode = :barcode AND is_active = 1 LIMIT 1")
    suspend fun getItemByBarcode(barcode: String): Item?
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItem(item: Item): Long
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItems(items: List<Item>): List<Long>
    @Update suspend fun updateItem(item: Item)
    @Query("UPDATE items SET current_quantity = current_quantity + :delta, updated_at = :updatedAt WHERE id = :itemId")
    suspend fun adjustQuantity(itemId: Long, delta: Double, updatedAt: Long = System.currentTimeMillis())
    @Query("UPDATE items SET selling_price = :price, updated_at = :updatedAt WHERE id = :itemId")
    suspend fun updateItemPrice(itemId: Long, price: Double, updatedAt: Long = System.currentTimeMillis())
    @Query("UPDATE items SET is_active = 0, updated_at = :updatedAt WHERE id = :itemId")
    suspend fun archiveItem(itemId: Long, updatedAt: Long = System.currentTimeMillis())
    @Query("SELECT COUNT(*) FROM items WHERE is_active = 1")
    suspend fun getItemCount(): Int
    @Query("SELECT * FROM items WHERE current_quantity <= min_quantity AND is_active = 1 ORDER BY name")
    fun getLowStockItems(): Flow<List<Item>>
    @Query("SELECT COALESCE(SUM(current_quantity * unit_cost), 0) FROM items WHERE is_active = 1")
    suspend fun getTotalInventoryValue(): Double
}
