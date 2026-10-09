package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.model.Account
import com.aalmoghalis.muhasibsoft.data.model.Voucher
import com.aalmoghalis.muhasibsoft.data.repository.AccountRepository
import com.aalmoghalis.muhasibsoft.data.repository.VoucherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoucherViewModel @Inject constructor(
    private val voucherRepository: VoucherRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoucherUiState())
    val uiState: StateFlow<VoucherUiState> = _uiState.asStateFlow()

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getVouchers(type: String): StateFlow<List<Voucher>> {
        return voucherRepository.getVouchersByType(type)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    /**
     * إنشاء سند قبض أو صرف
     * قبض: يزيد رصيد الصندوق
     * صرف: ينقص رصيد الصندوق
     */
    fun createVoucher(voucher: Voucher) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // حفظ السند
                voucherRepository.insertVoucher(voucher)

                // تحديث رصيد الحساب المرتبط
                voucher.accountId?.let { accountId ->
                    val delta = if (voucher.type == "receipt") -voucher.amount
                                else voucher.amount
                    accountRepository.updateAccountBalance(accountId, delta)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = if (voucher.type == "receipt")
                            "تم حفظ سند القبض بنجاح"
                        else "تم حفظ سند الصرف بنجاح"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun deleteVoucher(id: Long) {
        viewModelScope.launch {
            try {
                voucherRepository.deleteVoucher(id)
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

data class VoucherUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)