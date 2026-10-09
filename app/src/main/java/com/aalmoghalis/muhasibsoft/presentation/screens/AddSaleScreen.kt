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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.data.model.*
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.SalesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(
    onNavigateBack: () -> Unit,
    viewModel: SalesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var invoiceNumber by remember { mutableStateOf(uiState.nextInvoiceNumber) }
    var saleType by remember { mutableStateOf("cash") }
    var customerId by remember { mutableStateOf<Long?>(null) }
    var selectedItems by remember { mutableStateOf<List<SaleItem>>(emptyList()) }
    var showItemDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    
    val subtotal = selectedItems.sumOf { it.total }
    val tax = subtotal * 0.15 // 15% VAT
    val total = subtotal + tax
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "بيع جديد",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = {
                        val sale = Sale(
                            invoiceNumber = invoiceNumber,
                            type = saleType,
                            customerId = customerId,
                            subtotal = subtotal,
                            tax = tax,
                            total = total,
                            notes = notes
                        )
                        viewModel.createSale(sale, selectedItems)
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "معلومات الفاتورة",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        AppTextField(
                            value = invoiceNumber,
                            onValueChange = { invoiceNumber = it },
                            label = "رقم الفاتورة",
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // نوع البيع
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilterChip(
                                selected = saleType == "cash",
                                onClick = { saleType = "cash" },
                                label = { Text("نقدي") }
                            )
                            
                            FilterChip(
                                selected = saleType == "credit",
                                onClick = { saleType = "credit" },
                                label = { Text("آجل") }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // اختيار العميل
                        if (saleType == "credit") {
                            var expanded by remember { mutableStateOf(false) }
                            
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = uiState.customers.find { it.id == customerId }?.name ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("العميل") },
                                    trailingIcon = {
                                        Icon(Icons.Default.ArrowDropDown, null)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                                ) {
                                    uiState.customers.forEach { customer ->
                                        DropdownMenuItem(
                                            text = { Text(customer.name) },
                                            onClick = {
                                                customerId = customer.id
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
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
                                text = "الأصناف",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            IconButton(onClick = { showItemDialog = true }) {
                                Icon(Icons.Default.Add, "إضافة صنف")
                            }
                        }
                        
                        if (selectedItems.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لم تتم إضافة أصناف بعد",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            selectedItems.forEach { saleItem ->
                                val item = uiState.items.find { it.id == saleItem.itemId }
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = item?.name ?: "صنف",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        Text(
                                            text = "${saleItem.quantity} × ${saleItem.price}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${saleItem.total} ريال",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        IconButton(onClick = {
                                            selectedItems = selectedItems.filter { it != saleItem }
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                "حذف",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                                
                                Divider()
                            }
                        }
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "الإجماليات",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("المجموع الفرعي:")
                            Text("${String.format("%.2f", subtotal)} ريال")
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الضريبة (15%):")
                            Text("${String.format("%.2f", tax)} ريال")
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "الإجمالي:",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${String.format("%.2f", total)} ريال",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            item {
                AppTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "ملاحظات",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                AppButton(
                    text = "حفظ البيع",
                    onClick = {
                        val sale = Sale(
                            invoiceNumber = invoiceNumber,
                            type = saleType,
                            customerId = customerId,
                            subtotal = subtotal,
                            tax = tax,
                            total = total,
                            notes = notes
                        )
                        viewModel.createSale(sale, selectedItems)
                        onNavigateBack()
                    },
                    isLoading = uiState.isLoading
                )
            }
        }
    }
    
    if (showItemDialog) {
        AddItemDialog(
            items = uiState.filteredItems,
            onDismiss = { showItemDialog = false },
            onItemAdded = { item, quantity, price ->
                val saleItem = SaleItem(
                    itemId = item.id,
                    quantity = quantity,
                    price = price,
                    total = quantity * price
                )
                selectedItems = selectedItems + saleItem
                showItemDialog = false
            }
        )
    }
}

@Composable
fun AddItemDialog(
    items: List<Item>,
    onDismiss: () -> Unit,
    onItemAdded: (Item, Double, Double) -> Unit
) {
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة صنف") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("بحث عن صنف") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // قائمة الأصناف
                items
                    .filter { it.name.contains(searchQuery, ignoreCase = true) }
                    .take(5)
                    .forEach { item ->
                        TextButton(
                            onClick = {
                                selectedItem = item
                                price = item.sellingPrice.toString()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${item.name} - ${item.sellingPrice} ريال",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                selectedItem?.let { item ->
                    Text(
                        text = "الصنف: ${item.name}",
                        style = MaterialTheme.typography.titleSmall
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
                        value = price,
                        onValueChange = { price = it },
                        label = "السعر",
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
                        val prc = price.toDoubleOrNull() ?: 0.0
                        if (qty > 0 && prc > 0) {
                            onItemAdded(item, qty, prc)
                        }
                    }
                },
                enabled = selectedItem != null && quantity.isNotEmpty() && price.isNotEmpty()
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