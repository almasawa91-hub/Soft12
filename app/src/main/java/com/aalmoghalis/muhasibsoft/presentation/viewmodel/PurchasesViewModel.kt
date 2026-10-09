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
class PurchasesViewModel @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val itemRepository: ItemRepository,
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchasesUiState())
    val uiState: StateFlow<PurchasesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    val purchases: StateFlow<List<PurchaseWithItems>> = purchaseRepository.getAllPurchases()
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
                val suppliers = contactRepository.getContactsByType("supplier").first()
                val nextInvoiceNumber = purchaseRepository.generateInvoiceNumber()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = items,
                        filteredItems = items,
                        suppliers = suppliers,
                        nextInvoiceNumber = nextInvoiceNumber
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
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

    fun createPurchase(purchase: Purchase, items: List<PurchaseItem>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                purchaseRepository.insertPurchase(purchase, items)
                _uiState.update {
                    it.copy(isLoading = false, successMessage = "تم حفظ الشراء بنجاح")
                }
                loadInitialData()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "حدث خطأ أثناء الحفظ")
                }
            }
        }
    }

    fun deletePurchase(purchaseId: Long) {
        viewModelScope.launch {
            try {
                purchaseRepository.deletePurchase(purchaseId)
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

data class PurchasesUiState(
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList(),
    val filteredItems: List<Item> = emptyList(),
    val suppliers: List<Contact> = emptyList(),
    val nextInvoiceNumber: String = "#1",
    val successMessage: String? = null,
    val error: String? = null
)