package com.aalmoghalis.muhasibsoft.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.local.ItemDao
import com.aalmoghalis.muhasibsoft.data.local.SaleDao
import com.aalmoghalis.muhasibsoft.data.model.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
data class MainUiState(
    val isLoading: Boolean = true,
    val items: List<Item> = emptyList(),
    val itemCount: Int = 0,
    val saleCount: Int = 0,
    val inventoryValue: Double = 0.0,
    val error: String? = null
)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val itemDao: ItemDao,
    private val saleDao: SaleDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    init {
        refresh()
        viewModelScope.launch {
            itemDao.getAllItems()
                .catch { error -> _uiState.update { it.copy(isLoading = false, error = error.message) } }
                .collect { items -> _uiState.update { it.copy(isLoading = false, items = items, itemCount = items.size, error = null) } }
        }
    }
    fun addItem(name: String, barcode: String?, quantity: Double, cost: Double, price: Double, onComplete: (Boolean, String?) -> Unit) {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) {
            onComplete(false, "اسم الصنف مطلوب")
            return
        }
        if (quantity < 0.0 || cost < 0.0 || price < 0.0) {
            onComplete(false, "لا يمكن إدخال قيم سالبة")
            return
        }
        viewModelScope.launch {
            try {
                itemDao.insertItem(Item(
                    name = cleanName,
                    barcode = barcode?.trim()?.takeIf { it.isNotEmpty() },
                    currentQuantity = quantity,
                    openingQuantity = quantity,
                    unitCost = cost,
                    sellingPrice = price
                ))
                refresh()
                onComplete(true, null)
            } catch (error: Exception) {
                onComplete(false, error.localizedMessage ?: "تعذر حفظ الصنف")
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                val count = itemDao.getItemCount()
                val sales = saleDao.getSaleCount()
                val value = itemDao.getTotalInventoryValue()
                _uiState.update { it.copy(isLoading = false, itemCount = count, saleCount = sales, inventoryValue = value, error = null) }
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, error = error.localizedMessage ?: "تعذر تحميل البيانات") }
            }
        }
    }
}
