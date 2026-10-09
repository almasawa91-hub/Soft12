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
class SalesViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val itemRepository: ItemRepository,
    private val contactRepository: ContactRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    
    val sales: StateFlow<List<SaleWithItems>> = saleRepository.getAllSales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init {
        loadInitialData()
        setupSearch()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val items = itemRepository.getAllItems().first()
                val customers = contactRepository.getContactsByType("customer").first()
                val nextInvoiceNumber = saleRepository.generateInvoiceNumber()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = items,
                        customers = customers,
                        nextInvoiceNumber = nextInvoiceNumber
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }
    
    private fun setupSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isEmpty()) {
                        _uiState.update { it.copy(filteredItems = it.items) }
                    } else {
                        itemRepository.searchItems(query).collect { items ->
                            _uiState.update { it.copy(filteredItems = items) }
                        }
                    }
                }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun createSale(sale: Sale, items: List<SaleItem>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                saleRepository.insertSale(sale, items)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = "تم حفظ البيع بنجاح"
                    )
                }
                loadInitialData()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "حدث خطأ أثناء الحفظ"
                    )
                }
            }
        }
    }
    
    fun deleteSale(saleId: Long) {
        viewModelScope.launch {
            try {
                saleRepository.deleteSale(saleId)
                _uiState.update { it.copy(successMessage = "تم الحذف بنجاح") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}

data class SalesUiState(
    val isLoading: Boolean = false,
    val sales: List<SaleWithItems> = emptyList(),
    val items: List<Item> = emptyList(),
    val filteredItems: List<Item> = emptyList(),
    val customers: List<Contact> = emptyList(),
    val nextInvoiceNumber: String = "#1",
    val successMessage: String? = null,
    val error: String? = null
)