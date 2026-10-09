package com.aalmoghalis.muhasibsoft.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.data.model.PurchaseWithItems
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.PurchasesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddPurchase: () -> Unit,
    viewModel: PurchasesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val purchases by viewModel.purchases.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<PurchaseWithItems?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "قائمة المشتريات",
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
            FloatingActionButton(onClick = onNavigateToAddPurchase) {
                Icon(Icons.Default.Add, "إضافة شراء")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading && purchases.isEmpty() -> LoadingScreen()

                purchases.isEmpty() -> EmptyScreen("لا توجد مشتريات")

                else -> {
                    // رأس الجدول
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("رقم", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.weight(1f))
                        Text("التاريخ", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.weight(1.5f))
                        Text("الاسم", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.weight(1.5f))
                        Text("المبلغ", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.weight(1f))
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(purchases) { purchaseWithItems ->
                            PurchaseRow(
                                purchase = purchaseWithItems,
                                onClick = { },
                                onDelete = { showDeleteDialog = purchaseWithItems }
                            )
                        }
                    }
                }
            }
        }
    }

    // حوار تأكيد الحذف
    showDeleteDialog?.let { purchase ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("تأكيد الحذف") },
            text = { Text("هل أنت متأكد من حذف فاتورة الشراء ${purchase.purchase.invoiceNumber}؟") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePurchase(purchase.purchase.id)
                    showDeleteDialog = null
                }) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun PurchaseRow(
    purchase: PurchaseWithItems,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = purchase.purchase.invoiceNumber.replace("#", ""),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
                    .format(Date(purchase.purchase.date)),
                modifier = Modifier.weight(1.5f),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = purchase.supplier?.name ?: "نقدي",
                modifier = Modifier.weight(1.5f),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = String.format("%.0f", purchase.purchase.total),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Delete,
                    "حذف",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}