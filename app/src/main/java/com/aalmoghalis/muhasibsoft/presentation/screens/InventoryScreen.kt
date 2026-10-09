package com.aalmoghalis.muhasibsoft.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.data.model.Item
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val items by viewModel.items.collectAsState()
    var showAddWarehouseDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "العمليات المخزنية",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // بطاقة إجمالي المخزون
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "إجمالي قيمة المخزون",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = String.format("%.2f", uiState.totalInventoryValue),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // قسم العمليات
            item {
                SectionHeader("العمليات المخزنية")
            }

            item {
                InventoryMenuItem(
                    title = "صرف مخزني",
                    icon = Icons.Default.RemoveShoppingCart,
                    onClick = { }
                )
            }

            item {
                InventoryMenuItem(
                    title = "توريد مخزني",
                    icon = Icons.Default.LocalShipping,
                    onClick = { }
                )
            }

            item {
                InventoryMenuItem(
                    title = "تحويل مخزني",
                    icon = Icons.Default.SwapHoriz,
                    onClick = { }
                )
            }

            item {
                InventoryMenuItem(
                    title = "تسوية مخزنية",
                    icon = Icons.Default.Balance,
                    onClick = { }
                )
            }

            item {
                InventoryMenuItem(
                    title = "جرد مخزني",
                    icon = Icons.Default.FactCheck,
                    onClick = { }
                )
            }

            // قسم المخازن
            item {
                SectionHeader("المخازن")
            }

            item {
                InventoryMenuItem(
                    title = "إضافة مخزن",
                    icon = Icons.Default.AddBusiness,
                    onClick = { showAddWarehouseDialog = true }
                )
            }

            // قسم الأصناف
            item {
                SectionHeader("الأصناف (${items.size})")
            }

            items(items) { item ->
                ItemRow(item = item)
            }

            item {
                Button(
                    onClick = { showAddItemDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إضافة صنف جديد")
                }
            }
        }
    }

    if (showAddWarehouseDialog) {
        AddWarehouseDialog(
            onDismiss = { showAddWarehouseDialog = false },
            onConfirm = { name, phone, address ->
                viewModel.addWarehouse(name, phone, address)
                showAddWarehouseDialog = false
            }
        )
    }

    if (showAddItemDialog) {
        AddItemDialogFull(
            onDismiss = { showAddItemDialog = false },
            onConfirm = { item ->
                viewModel.addItem(item)
                showAddItemDialog = false
            }
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun InventoryMenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ItemRow(item: Item) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "التكلفة: ${item.unitCost} | البيع: ${item.sellingPrice}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.0f", item.currentQuantity),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (item.currentQuantity <= item.minQuantity)
                        MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "الكمية",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun AddWarehouseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مخزن") },
        text = {
            Column {
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "اسم المخزن",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "رقم الهاتف",
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "العنوان",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, phone.ifBlank { null }, address.ifBlank { null })
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("موافق")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun AddItemDialogFull(
    onDismiss: () -> Unit,
    onConfirm: (Item) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var openingQty by remember { mutableStateOf("") }
    var unitCost by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var minQty by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة صنف جديد") },
        text = {
            Column {
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "اسم الصنف",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = openingQty,
                    onValueChange = { openingQty = it },
                    label = "الكمية الافتتاحية",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = unitCost,
                    onValueChange = { unitCost = it },
                    label = "تكلفة الوحدة",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = sellingPrice,
                    onValueChange = { sellingPrice = it },
                    label = "سعر البيع",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = minQty,
                    onValueChange = { minQty = it },
                    label = "الحد الأدنى للكمية",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val qty = openingQty.toDoubleOrNull() ?: 0.0
                    val item = Item(
                        name = name,
                        openingQuantity = qty,
                        currentQuantity = qty,
                        unitCost = unitCost.toDoubleOrNull() ?: 0.0,
                        sellingPrice = sellingPrice.toDoubleOrNull() ?: 0.0,
                        minQuantity = minQty.toDoubleOrNull() ?: 0.0
                    )
                    onConfirm(item)
                },
                enabled = name.isNotBlank()
            ) {
                Text("موافق")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}