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
import com.aalmoghalis.muhasibsoft.data.model.Account
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.AccountsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToJournal: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("الحسابات", "القيود اليومية")

    Scaffold(
        topBar = {
            AppTopBar(
                title = "الحسابات",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "إضافة حساب")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // التبويبات
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> AccountsList(
                    accounts = accounts,
                    onDelete = { viewModel.deleteAccount(it) }
                )
                1 -> JournalEntriesList(onNavigateToJournal)
            }
        }
    }

    if (showAddDialog) {
        AddAccountDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, notes ->
                viewModel.addAccount(name, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AccountsList(
    accounts: List<Account>,
    onDelete: (Long) -> Unit
) {
    if (accounts.isEmpty()) {
        EmptyScreen("لا توجد حسابات")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(accounts) { account ->
                AccountCard(
                    account = account,
                    onDelete = { onDelete(account.id) }
                )
            }
        }
    }
}

@Composable
fun AccountCard(
    account: Account,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleMedium
                )

                account.notes?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.2f", account.balance),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (account.balance >= 0)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )

                Text(
                    text = if (account.currency == "local") "محلي" else "دولار",
                    style = MaterialTheme.typography.bodySmall
                )
            }

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

@Composable
fun JournalEntriesList(onNavigateToJournal: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "القيود اليومية",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateToJournal) {
            Icon(Icons.Default.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("قيد جديد")
        }
    }
}

@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة حساب جديد") },
        text = {
            Column {
                AppTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isError = false
                    },
                    label = "اسم الحساب",
                    isError = isError,
                    errorMessage = if (isError) "هذا الحقل مطلوب" else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                AppTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "ملاحظات (اختياري)",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(name, notes.ifBlank { null })
                    }
                }
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