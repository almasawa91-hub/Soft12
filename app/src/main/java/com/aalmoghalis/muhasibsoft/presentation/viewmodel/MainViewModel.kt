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
