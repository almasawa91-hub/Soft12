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
import com.aalmoghalis.muhasibsoft.data.model.Currency
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.CurrencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrenciesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CurrencyViewModel = hiltViewModel()
) {
    val currencies by viewModel.currencies.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCurrency by remember { mutableStateOf<Currency?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "العملات",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "إضافة عملة")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (currencies.isEmpty()) {
                EmptyScreen("لا توجد عملات")
            } else {
                // رأس الجدول
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(12.dp)
                ) {
                    Text("العملة", color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1.2f))
                    Text("الفئة", color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f))
                    Text("رمز العملة", color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f))
                    Text("سعر الصرف", color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(currencies) { currency ->
                        CurrencyRow(
                            currency = currency,
                            onEditRate = { editingCurrency = currency }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCurrencyDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, symbol, code, rate ->
                viewModel.addCurrency(name, symbol, code, rate)
                showAddDialog = false
            }
        )
    }

    editingCurrency?.let { currency ->
        EditExchangeRateDialog(
            currency = currency,
            onDismiss = { editingCurrency = null },
            onConfirm = { rate ->
                viewModel.updateExchangeRate(currency.id, rate)
                editingCurrency = null
            }
        )
    }
}

@Composable
fun CurrencyRow(
    currency: Currency,
    onEditRate: () -> Unit
) {
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
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = currency.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (currency.isDefault) {
                    Text(
                        text = "العملة الافتراضية",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "فلس/سنت",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = currency.code,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%.2f", currency.exchangeRate),
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = onEditRate, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        "تعديل السعر",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun AddCurrencyDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var symbol by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("1.0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة عملة") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "اسم العملة",
                    modifier = Modifier.fillMaxWidth()
                )
                AppTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = "الرمز (مثال: ر.ي)",
                    modifier = Modifier.fillMaxWidth()
                )
                AppTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = "الكود (مثال: YR)",
                    modifier = Modifier.fillMaxWidth()
                )
                AppTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = "سعر الصرف مقابل العملة الأساسية",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        name,
                        symbol,
                        code,
                        rate.toDoubleOrNull() ?: 1.0
                    )
                },
                enabled = name.isNotBlank() && code.isNotBlank()
            ) {
                Text("موافق")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
fun EditExchangeRateDialog(
    currency: Currency,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var rate by remember { mutableStateOf(currency.exchangeRate.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل سعر الصرف: ${currency.name}") },
        text = {
            AppTextField(
                value = rate,
                onValueChange = { rate = it },
                label = "سعر الصرف الجديد",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    rate.toDoubleOrNull()?.let { onConfirm(it) }
                },
                enabled = rate.toDoubleOrNull() != null
            ) {
                Text("موافق")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}