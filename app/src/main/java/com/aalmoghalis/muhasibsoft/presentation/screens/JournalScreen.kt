package com.aalmoghalis.muhasibsoft.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.data.model.Account
import com.aalmoghalis.muhasibsoft.data.model.JournalEntry
import com.aalmoghalis.muhasibsoft.data.model.JournalEntryDetail
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.AccountsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    var currency by remember { mutableStateOf("local") }
    var description by remember { mutableStateOf("") }
    var entryLines by remember {
        mutableStateOf(listOf(JournalLine(), JournalLine()))
    }

    val totalDebit = entryLines.sumOf { it.debit.toDoubleOrNull() ?: 0.0 }
    val totalCredit = entryLines.sumOf { it.credit.toDoubleOrNull() ?: 0.0 }
    val isBalanced = totalDebit == totalCredit && totalDebit > 0

    Scaffold(
        topBar = {
            AppTopBar(
                title = "قيود يومية",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = {
                            if (isBalanced) {
                                val entry = JournalEntry(
                                    referenceNumber = "#${System.currentTimeMillis() % 10000}",
                                    description = description,
                                    currency = currency
                                )
                                val details = entryLines
                                    .filter { it.accountId != null }
                                    .map { line ->
                                        JournalEntryDetail(
                                            accountId = line.accountId!!,
                                            debit = line.debit.toDoubleOrNull() ?: 0.0,
                                            credit = line.credit.toDoubleOrNull() ?: 0.0,
                                            notes = line.notes
                                        )
                                    }
                                viewModel.addJournalEntry(entry, details)
                                onNavigateBack()
                            }
                        },
                        enabled = isBalanced
                    ) {
                        Icon(Icons.Default.Save, "حفظ القيد")
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
            // بطاقة رأس القيد
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // اختيار العملة
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currency == "local",
                                onClick = { currency = "local" }
                            )
                            Text("محلي")

                            Spacer(modifier = Modifier.width(24.dp))

                            RadioButton(
                                selected = currency == "usd",
                                onClick = { currency = "usd" }
                            )
                            Text("دولار")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "التاريخ: ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale("ar")).format(java.util.Date())}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AppTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "البيان",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // سطور القيد
            itemsIndexed(entryLines) { index, line ->
                JournalLineCard(
                    line = line,
                    accounts = accounts,
                    onLineChange = { newLine ->
                        entryLines = entryLines.toMutableList().also { it[index] = newLine }
                    },
                    onRemove = if (entryLines.size > 2) {
                        { entryLines = entryLines.filterIndexed { i, _ -> i != index } }
                    } else null
                )
            }

            // زر إضافة سطر
            item {
                OutlinedButton(
                    onClick = { entryLines = entryLines + JournalLine() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إضافة سطر")
                }
            }

            // بطاقة التوازن
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBalanced)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("إجمالي المدين: ${String.format("%.2f", totalDebit)}")
                            Text("إجمالي الدائن: ${String.format("%.2f", totalCredit)}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBalanced) "✓ القيد متوازن" else "✗ القيد غير متوازن",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isBalanced)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

data class JournalLine(
    val accountId: Long? = null,
    val accountName: String = "",
    val debit: String = "",
    val credit: String = "",
    val notes: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalLineCard(
    line: JournalLine,
    accounts: List<Account>,
    onLineChange: (JournalLine) -> Unit,
    onRemove: (() -> Unit)?
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // اختيار الحساب
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = line.accountName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("الحساب") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        accounts.forEach { account ->
                            DropdownMenuItem(
                                text = { Text(account.name) },
                                onClick = {
                                    onLineChange(
                                        line.copy(
                                            accountId = account.id,
                                            accountName = account.name
                                        )
                                    )
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                onRemove?.let {
                    IconButton(onClick = it) {
                        Icon(
                            Icons.Default.Close,
                            "حذف السطر",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppTextField(
                    value = line.debit,
                    onValueChange = { onLineChange(line.copy(debit = it)) },
                    label = "مدين",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                AppTextField(
                    value = line.credit,
                    onValueChange = { onLineChange(line.copy(credit = it)) },
                    label = "دائن",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}