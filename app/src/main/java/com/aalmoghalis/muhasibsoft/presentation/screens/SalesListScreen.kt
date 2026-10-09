package com.aalmoghalis.muhasibsoft.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.data.model.SaleWithItems
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.SalesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddSale: () -> Unit,
    viewModel: SalesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sales by viewModel.sales.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "قائمة المبيعات",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, "بحث")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.PictureAsPdf, "تصدير PDF")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddSale) {
                Icon(Icons.Default.Add, "إضافة بيع")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // شريط البحث
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.updateSearchQuery(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("بحث...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )
            
            when {
                uiState.isLoading -> LoadingScreen()
                
                sales.isEmpty() -> EmptyScreen("لا توجد مبيعات")
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sales) { saleWithItems ->
                            SaleCard(
                                sale = saleWithItems,
                                onClick = { },
                                onDelete = { viewModel.deleteSale(saleWithItems.sale.id) }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // رسائل النجاح والخطأ
    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let {
            // Show snackbar
            viewModel.clearMessages()
        }
        uiState.error?.let {
            // Show error
            viewModel.clearMessages()
        }
    }
}

@Composable
fun SaleCard(
    sale: SaleWithItems,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = sale.sale.invoiceNumber,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
                        .format(Date(sale.sale.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                sale.customer?.let {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(
                    text = "${sale.sale.total} ريال",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = if (sale.sale.type == "cash") "نقدي" else "آجل",
                    style = MaterialTheme.typography.bodySmall
                )
                
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        "حذف",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}