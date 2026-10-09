package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.model.*
import com.aalmoghalis.muhasibsoft.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val warehouseRepository: WarehouseRepository,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    val items: StateFlow<List<Item>> = itemRepository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val warehouses: StateFlow<List<Warehouse>> = warehouseRepository.getAllWarehouses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val totalValue = itemRepository.getTotalInventoryValue() ?: 0.0
            _uiState.update { it.copy(totalInventoryValue = totalValue) }
        }
    }

    fun addWarehouse(name: String, phone: String?, address: String?) {
        viewModelScope.launch {
            try {
                val warehouse = Warehouse(
                    name = name,
                    phone = phone,
                    address = address
                )
                warehouseRepository.insertWarehouse(warehouse)
                _uiState.update { it.copy(successMessage = "تم إضافة المخزن بنجاح") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            try {
                itemRepository.insertItem(item)
                _uiState.update { it.copy(successMessage = "تم إضافة الصنف بنجاح") }
                loadStats()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun performInventoryOperation(
        operation: InventoryOperation,
        items: List<InventoryOperationItem>
    ) {
        viewModelScope.launch {
            try {
                inventoryRepository.insertOperation(operation, items)

                // تحديث الكميات حسب نوع العملية
                items.forEach { opItem ->
                    val delta = when (operation.type) {
                        "supply" -> opItem.quantity      // توريد: زيادة
                        "dispense" -> -opItem.quantity   // صرف: نقص
                        else -> 0.0
                    }
                    itemRepository.updateItemQuantity(opItem.itemId, delta)
                }

                _uiState.update { it.copy(successMessage = "تم تنفيذ العملية بنجاح") }
                loadStats()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}

data class InventoryUiState(
    val isLoading: Boolean = false,
    val totalInventoryValue: Double = 0.0,
    val successMessage: String? = null,
    val error: String? = null
)