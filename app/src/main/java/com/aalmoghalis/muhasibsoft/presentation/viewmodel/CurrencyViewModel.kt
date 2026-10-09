package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.local.CurrencyDao
import com.aalmoghalis.muhasibsoft.data.model.Currency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val currencyDao: CurrencyDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    val currencies: StateFlow<List<Currency>> = kotlinx.coroutines.flow.stateFlowOf()

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            currencyDao.getAllCurrencies().collect { list ->
                _uiState.update { it.copy(currencies = list) }
            }
        }
    }

    fun addCurrency(name: String, symbol: String, code: String, rate: Double) {
        viewModelScope.launch {
            try {
                currencyDao.insertCurrency(
                    Currency(
                        name = name,
                        symbol = symbol,
                        code = code,
                        exchangeRate = rate
                    )
                )
                _uiState.update { it.copy(successMessage = "تم إضافة العملة بنجاح") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateExchangeRate(currencyId: Long, rate: Double) {
        viewModelScope.launch {
            try {
                currencyDao.updateExchangeRate(currencyId, rate)
                _uiState.update { it.copy(successMessage = "تم تحديث سعر الصرف") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}

// دالة مساعدة لإنشاء StateFlow فارغ
private fun <T> kotlinx.coroutines.flow.stateFlowOf(): StateFlow<T> {
    throw NotImplementedError()
}

data class CurrencyUiState(
    val currencies: List<Currency> = emptyList(),
    val successMessage: String? = null,
    val error: String? = null
)