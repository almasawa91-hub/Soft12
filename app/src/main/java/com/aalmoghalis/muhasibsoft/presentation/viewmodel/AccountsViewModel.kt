package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.model.Account
import com.aalmoghalis.muhasibsoft.data.model.JournalEntry
import com.aalmoghalis.muhasibsoft.data.model.JournalEntryDetail
import com.aalmoghalis.muhasibsoft.data.repository.AccountRepository
import com.aalmoghalis.muhasibsoft.data.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<JournalEntry>> = journalRepository.getAllJournalEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAccount(name: String, notes: String?) {
        viewModelScope.launch {
            try {
                val account = Account(
                    name = name,
                    notes = notes
                )
                accountRepository.insertAccount(account)
                _uiState.update { it.copy(successMessage = "تم إضافة الحساب بنجاح") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun addJournalEntry(entry: JournalEntry, details: List<JournalEntryDetail>) {
        viewModelScope.launch {
            try {
                // التحقق من توازن القيد
                val totalDebit = details.sumOf { it.debit }
                val totalCredit = details.sumOf { it.credit }

                if (totalDebit != totalCredit) {
                    _uiState.update {
                        it.copy(error = "القيد غير متوازن: مدين $totalDebit ≠ دائن $totalCredit")
                    }
                    return@launch
                }

                journalRepository.insertJournalEntry(entry, details)
                _uiState.update { it.copy(successMessage = "تم حفظ القيد بنجاح") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateAccountBalance(accountId: Long, amount: Double) {
        viewModelScope.launch {
            accountRepository.updateAccountBalance(accountId, amount)
        }
    }

    fun deleteAccount(accountId: Long) {
        viewModelScope.launch {
            accountRepository.deleteAccount(accountId)
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}

data class AccountsUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)