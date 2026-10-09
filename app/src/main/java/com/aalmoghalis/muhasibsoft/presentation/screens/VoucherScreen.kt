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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.data.model.Account
import com.aalmoghalis.muhasibsoft.data.model.Voucher
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.VoucherViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * شاشة سند قبض / صرف
 * @param voucherType "receipt" للقبض، "payment" للصرف
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(
    voucherType: String,
    onNavigateBack: () -> Unit,
    viewModel: VoucherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    val isReceipt = voucherType == "receipt"
    val title = if (isReceipt) "سند قبض" else "سند صرف"

    var voucherNumber by remember { mutableStateOf("#1") }
    var description by remember { mutableStateOf("") }
    var accountId by remember { mutableStateOf<Long?>(null) }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showAccountDialog by remember { mutableStateOf(false) }

    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val selectedAccount = accounts.find { it.id == accountId }

    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = {
                            val voucher = Voucher(
                                type = voucherType,
                                number = voucherNumber,
                                accountId = accountId,
                                amount = amountValue,
                                description = description,
                                notes = notes.ifBlank { null }
                            )
                            viewModel.createVoucher(voucher)
                            onNavigateBack()
                        },
                        enabled = amountValue > 0 && accountId != null
                    ) {
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
            // بطاقة معلومات السند
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isReceipt) "قبض" else "صرف",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isReceipt)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "الصندوق",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = SimpleDateFormat("dd-MM-yyyy", Locale("ar"))
                                        .format(Date()),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "رقم: $voucherNumber",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AppTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "البيان",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // بطاقة الحساب والمبلغ
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // اختيار الحساب
                        OutlinedButton(
                            onClick = { showAccountDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedAccount?.name ?: "اختر الحساب",
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start
                            )
                            Icon(Icons.Default.ArrowDropDown, null)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // المبلغ
                        AppTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = "المبلغ",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // الملاحظات
                        AppTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = "ملاحظات (اختياري)",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ملخص السند
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isReceipt)
                            Color(0xFFE8F5E9)
                        else
                            Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إجمالي السند:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = String.format("%.2f", amountValue),
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (isReceipt)
                                Color(0xFF2E7D32)
                            else
                                Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }

    // حوار اختيار الحساب
    if (showAccountDialog) {
        AccountPickerDialog(
            accounts = accounts,
            onDismiss = { showAccountDialog = false },
            onSelect = { account ->
                accountId = account.id
                showAccountDialog = false
            }
        )
    }
}

@Composable
fun AccountPickerDialog(
    accounts: List<Account>,
    onDismiss: () -> Unit,
    onSelect: (Account) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اختر الحساب") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("بحث") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(
                        accounts.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }
                    ) { account ->
                        ListItem(
                            headlineContent = { Text(account.name) },
                            supportingContent = {
                                Text("الرصيد: ${String.format("%.2f", account.balance)}")
                            },
                            leadingContent = {
                                Icon(Icons.Default.Person, null)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        // استخدام clickable عبر wrapper
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}