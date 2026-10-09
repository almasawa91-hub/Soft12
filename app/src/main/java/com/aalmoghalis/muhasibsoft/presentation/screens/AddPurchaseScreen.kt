package com.aalmoghalis.muhasibsoft.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.data.model.*
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.PurchasesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseScreen(
    onNavigateBack: () -> Unit,
    viewModel: PurchasesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var invoiceNumber by remember { mutableStateOf(uiState.nextInvoiceNumber) }
    var purchaseType by remember { mutableStateOf("credit") }
    var supplierId by remember { mutableStateOf<Long?>(null) }
    var selectedItems by remember { mutableStateOf<List<PurchaseItem>>(emptyList()) }
    var showItemDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var paidAmount by remember { mutableStateOf("") }

    val subtotal = selectedItems.sumOf { it.total }
    val discountValue = discount.toDoubleOrNull() ?: 0.0
    val total = subtotal - discountValue
    val paid = paidAmount.toDoubleOrNull() ?: 0.0
    val remaining = total - paid

    Scaffold(
        topBar = {
            AppTopBar(
                title = "شراء آجل",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = {
                        val purchase = Purchase(
                            invoiceNumber = invoiceNumber,
                            type = purchaseType,
                            supplierId = supplierId,
                            subtotal = subtotal,
                            discount = discountValue,
                            total = total,
                            paid = paid,
                            remaining = remaining,
                            notes = notes
                        )
                        viewModel.createPurchase(purchase, selectedItems)
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Save, "حفظ")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // بطاقة معلومات الفاتورة
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "آجل",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // مفتاح التبديل
                            Switch(
                                checked = purchaseType == "cash",
                                onCheckedChange = {
                                    purchaseType = if (it) "cash" else "credit"
                                }
                            )

                            Text(
                                text = "المخزن الرئيسي",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // اختيار المورد
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = uiState.suppliers.find { it.id == supplierId }?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("المورد") },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                uiState.suppliers.forEach { supplier ->
                                    DropdownMenuItem(
                                        text = { Text(supplier.name) },
                                        onClick = {
                                            supplierId = supplier.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "التاريخ: ${SimpleDateFormat("yyyy-MM-dd", Locale("ar")).format(Date())}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "رقم: $invoiceNumber",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // بطاقة الأصناف
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "اكتب اسم الصنف",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            IconButton(onClick = { showItemDialog = true }) {
                                Icon(Icons.Default.AddCircle, "إضافة صنف",
                                    tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Divider()

                        // رؤوس الجدول
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الصنف", modifier = Modifier.weight(2f),
                                style = MaterialTheme.typography.bodySmall)
                            Text("الكمية", modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall)
                            Text("السعر", modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall)
                            Text("الإجمالي", modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall)
                        }

                        Divider()

                        if (selectedItems.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لا توجد أصناف مضافة",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            selectedItems.forEach { purchaseItem ->
                                val item = uiState.items.find { it.id == purchaseItem.itemId }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item?.name ?: "-",
                                        modifier = Modifier.weight(2f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = String.format("%.0f", purchaseItem.quantity),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = String.format("%.0f", purchaseItem.cost),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = String.format("%.0f", purchaseItem.total),
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = {
                                            selectedItems = selectedItems.filter { it != purchaseItem }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            "حذف",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // بطاقة الخصم والمدفوع
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AppTextField(
                            value = discount,
                            onValueChange = { discount = it },
                            label = "الخصم",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AppTextField(
                            value = paidAmount,
                            onValueChange = { paidAmount = it },
                            label = "المبلغ المدفوع",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // بطاقة الإجماليات
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TotalRow("المجموع", subtotal)
                        TotalRow("الخصم", discountValue)
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        TotalRow("الصافي", total, isBold = true)
                        TotalRow("المدفوع", paid)
                        TotalRow("المتبقي", remaining, isBold = true)
                    }
                }
            }

            // الملاحظات
            item {
                AppTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "ملاحظات",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // زر الحفظ
            item {
                AppButton(
                    text = "حفظ الشراء",
                    onClick = {
                        val purchase = Purchase(
                            invoiceNumber = invoiceNumber,
                            type = purchaseType,
                            supplierId = supplierId,
                            subtotal = subtotal,
                            discount = discountValue,
                            total = total,
                            paid = paid,
                            remaining = remaining,
                            notes = notes
                        )
                        viewModel.createPurchase(purchase, selectedItems)
                        onNavigateBack()
                    },
                    isLoading = uiState.isLoading,
                    enabled = selectedItems.isNotEmpty()
                )
            }
        }
    }

    // حوار إضافة صنف للشراء
    if (showItemDialog) {
        AddPurchaseItemDialog(
            items = uiState.filteredItems,
            onDismiss = { showItemDialog = false },
            onItemAdded = { item, quantity, cost ->
                val purchaseItem = PurchaseItem(
                    itemId = item.id,
                    quantity = quantity,
                    cost = cost,
                    total = quantity * cost
                )
                selectedItems = selectedItems + purchaseItem
                showItemDialog = false
            }
        )
    }
}

@Composable
fun TotalRow(label: String, value: Double, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.titleMedium
                    else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = String.format("%.2f", value),
            style = if (isBold) MaterialTheme.typography.titleMedium
                    else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun AddPurchaseItemDialog(
    items: List<Item>,
    onDismiss: () -> Unit,
    onItemAdded: (Item, Double, Double) -> Unit
) {
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var quantity by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة صنف للمشتريات") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("بحث عن صنف") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                items
                    .filter { it.name.contains(searchQuery, ignoreCase = true) }
                    .take(5)
                    .forEach { item ->
                        TextButton(
                            onClick = {
                                selectedItem = item
                                cost = item.unitCost.toString()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${item.name} - تكلفة: ${item.unitCost}",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                selectedItem?.let { item ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "الصنف المحدد: ${item.name}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = "الكمية",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = "سعر التكلفة",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedItem?.let { item ->
                        val qty = quantity.toDoubleOrNull() ?: 0.0
                        val cst = cost.toDoubleOrNull() ?: 0.0
                        if (qty > 0 && cst > 0) {
                            onItemAdded(item, qty, cst)
                        }
                    }
                },
                enabled = selectedItem != null && quantity.isNotEmpty() && cost.isNotEmpty()
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}