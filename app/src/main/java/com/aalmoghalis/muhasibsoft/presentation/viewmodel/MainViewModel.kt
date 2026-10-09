package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val saleRepository: SaleRepository,
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val itemCount = itemRepository.getItemCount()
                val saleCount = saleRepository.getSaleCount()
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        itemCount = itemCount,
                        saleCount = saleCount
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    fun refresh() {
        loadDashboardData()
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val itemCount: Int = 0,
    val saleCount: Int = 0,
    val purchaseCount: Int = 0,
    val error: String? = null
)