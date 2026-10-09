package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.local.*
import com.aalmoghalis.muhasibsoft.data.model.*
import com.aalmoghalis.muhasibsoft.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val itemDao: ItemDao,
    private val saleDao: SaleDao,
    private val purchaseDao: PurchaseDao,
    private val accountDao: AccountDao,
    private val voucherDao: VoucherDao,
    private val journalEntryDao: JournalEntryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadInventoryReport()
    }

    /**
     * تقرير المخزون المتبقي
     */
    fun loadInventoryReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentReport = "المخزون المتبقي") }

            itemDao.getAllItems().collect { items ->
                val rows = items.map { item ->
                    ReportRow(
                        columns = listOf(
                            item.name,
                            String.format("%.0f", item.currentQuantity),
                            String.format("%.2f", item.unitCost),
                            String.format("%.2f", item.currentQuantity * item.unitCost)
                        )
                    )
                }

                val totalValue = items.sumOf { it.currentQuantity * it.unitCost }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        headers = listOf("الصنف", "الكمية", "التكلفة", "القيمة"),
                        rows = rows,
                        totalValue = totalValue
                    )
                }
            }
        }
    }

    /**
     * تقرير أرصدة الحسابات
     */
    fun loadAccountsReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentReport = "أرصدة الحسابات") }

            accountDao.getAllAccounts().collect { accounts ->
                val rows = accounts.map { account ->
                    ReportRow(
                        columns = listOf(
                            account.name,
                            if (account.balance >= 0) "مدين" else "دائن",
                            String.format("%.2f", kotlin.math.abs(account.balance))
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        headers = listOf("الحساب", "النوع", "الرصيد"),
                        rows = rows,
                        totalValue = accounts.sumOf { it.balance }
                    )
                }
            }
        }
    }

    /**
     * تقرير المبيعات اليومية
     */
    fun loadDailySalesReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentReport = "العمليات اليومية") }

            val today = System.currentTimeMillis()
            val startOfDay = DateUtils.startOfDay(today)
            val endOfDay = DateUtils.endOfDay(today)

            saleDao.getSalesByDateRange(startOfDay, endOfDay).collect { sales ->
                val rows = sales.map { sale ->
                    ReportRow(
                        columns = listOf(
                            sale.invoiceNumber,
                            DateUtils.formatDateTime(sale.date),
                            if (sale.type == "cash") "نقدي" else "آجل",
                            String.format("%.2f", sale.total)
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        headers = listOf("رقم", "التاريخ", "النوع", "المبلغ"),
                        rows = rows,
                        totalValue = sales.sumOf { it.total }
                    )
                }
            }
        }
    }

    /**
     * تقرير أرباح الأصناف
     */
    fun loadItemsProfitReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentReport = "أرباح الأصناف") }

            itemDao.getAllItems().collect { items ->
                val rows = items.map { item ->
                    val profit = item.sellingPrice - item.unitCost
                    ReportRow(
                        columns = listOf(
                            item.name,
                            String.format("%.2f", item.unitCost),
                            String.format("%.2f", item.sellingPrice),
                            String.format("%.2f", profit),
                            String.format("%.1f", if (item.unitCost > 0)
                                (profit / item.unitCost * 100) else 0.0) + "%"
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        headers = listOf("الصنف", "التكلفة", "البيع", "الربح", "النسبة"),
                        rows = rows,
                        totalValue = items.sumOf { it.sellingPrice - it.unitCost }
                    )
                }
            }
        }
    }

    /**
     * تقرير حركة الصندوق
     */
    fun loadCashFlowReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentReport = "حركة الصندوق") }

            voucherDao.getAllVouchers().collect { vouchers ->
                val rows = vouchers.map { voucher ->
                    ReportRow(
                        columns = listOf(
                            voucher.number,
                            DateUtils.formatDate(voucher.date),
                            if (voucher.type == "receipt") "قبض" else "صرف",
                            voucher.description ?: "-",
                            String.format("%.2f", voucher.amount)
                        )
                    )
                }

                val receipts = vouchers.filter { it.type == "receipt" }.sumOf { it.amount }
                val payments = vouchers.filter { it.type == "payment" }.sumOf { it.amount }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        headers = listOf("رقم", "التاريخ", "النوع", "البيان", "المبلغ"),
                        rows = rows,
                        totalValue = receipts - payments,
                        extraInfo = "إجمالي القبض: ${String.format("%.2f", receipts)} | إجمالي الصرف: ${String.format("%.2f", payments)}"
                    )
                }
            }
        }
    }

    /**
     * تقرير ميزان المراجعة (من القيود)
     */
    fun loadTrialBalanceReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentReport = "ميزان المراجعة") }

            accountDao.getAllAccounts().collect { accounts ->
                val rows = accounts.map { account ->
                    ReportRow(
                        columns = listOf(
                            account.name,
                            String.format("%.2f", maxOf(0.0, account.balance)),
                            String.format("%.2f", maxOf(0.0, -account.balance))
                        )
                    )
                }

                val totalDebit = accounts.sumOf { maxOf(0.0, it.balance) }
                val totalCredit = accounts.sumOf { maxOf(0.0, -it.balance) }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        headers = listOf("الحساب", "مدين", "دائن"),
                        rows = rows,
                        totalValue = totalDebit - totalCredit,
                        extraInfo = "إجمالي مدين: ${String.format("%.2f", totalDebit)} | إجمالي دائن: ${String.format("%.2f", totalCredit)}"
                    )
                }
            }
        }
    }

    /**
     * تصدير التقرير الحالي إلى PDF
     */
    fun exportCurrentReportToPdf(context: android.content.Context): Result<java.io.File> {
        val state = _uiState.value
        return com.aalmoghalis.muhasibsoft.utils.PdfExporter.exportReport(
            context = context,
            title = state.currentReport,
            headers = state.headers,
            rows = state.rows.map { it.columns },
            fileName = "report_${System.currentTimeMillis()}.pdf"
        )
    }
}

data class ReportsUiState(
    val isLoading: Boolean = false,
    val currentReport: String = "",
    val headers: List<String> = emptyList(),
    val rows: List<ReportRow> = emptyList(),
    val totalValue: Double = 0.0,
    val extraInfo: String? = null
)

data class ReportRow(
    val columns: List<String>
)