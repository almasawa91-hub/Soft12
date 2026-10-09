package com.aalmoghalis.muhasibsoft.presentation.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.ReportsViewModel

/**
 * شاشة عرض التقرير التفصيلي مع جدول البيانات
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    reportType: String,
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showExportMenu by remember { mutableStateOf(false) }

    // تحميل التقرير المطلوب
    LaunchedEffect(reportType) {
        when (reportType) {
            "inventory" -> viewModel.loadInventoryReport()
            "accounts" -> viewModel.loadAccountsReport()
            "daily_sales" -> viewModel.loadDailySalesReport()
            "items_profit" -> viewModel.loadItemsProfitReport()
            "cash_flow" -> viewModel.loadCashFlowReport()
            "trial_balance" -> viewModel.loadTrialBalanceReport()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = uiState.currentReport.ifBlank { "التقرير" },
                onBackClick = onNavigateBack,
                actions = {
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(Icons.Default.MoreVert, "خيارات")
                        }

                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("تصدير PDF") },
                                leadingIcon = { Icon(Icons.Default.PictureAsPdf, null) },
                                onClick = {
                                    showExportMenu = false
                                    exportAndShare(context, viewModel)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("تحديث التقرير") },
                                leadingIcon = { Icon(Icons.Default.Refresh, null) },
                                onClick = {
                                    showExportMenu = false
                                    viewModel.loadInventoryReport()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> LoadingScreen()

                uiState.rows.isEmpty() -> EmptyScreen("لا توجد بيانات للتقرير")

                else -> {
                    // رأس الجدول
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    ) {
                        uiState.headers.forEach { header ->
                            Text(
                                text = header,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // صفوف البيانات
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(uiState.rows) { row ->
                            ReportRowItem(row.columns)
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }

                    // معلومات إضافية
                    uiState.extraInfo?.let { info ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = info,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // صف الإجمالي
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الإجمالي:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = String.format("%.2f", uiState.totalValue),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportRowItem(columns: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        columns.forEach { cell ->
            Text(
                text = cell,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * تصدير التقرير ومشاركته
 */
private fun exportAndShare(context: Context, viewModel: ReportsViewModel) {
    viewModel.exportCurrentReportToPdf(context)
        .onSuccess { file ->
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(shareIntent, "مشاركة التقرير")
            )
        }
        .onFailure {
            android.widget.Toast.makeText(
                context,
                "فشل التصدير: ${it.message}",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
}