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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.data.model.User
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.UsersViewModel

/**
 * شاشة إدارة المستخدمين والصلاحيات
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    onNavigateBack: () -> Unit,
    viewModel: UsersViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "المستخدمين والصلاحيات",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.PersonAdd, "إضافة مستخدم")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                UserCard(
                    user = user,
                    onEdit = { editingUser = user },
                    onToggleActive = { viewModel.toggleUserActive(user) },
                    onDelete = { viewModel.deleteUser(user.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        UserDialog(
            title = "إضافة مستخدم",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, username, password, role ->
                viewModel.addUser(name, username, password, role)
                showAddDialog = false
            }
        )
    }

    editingUser?.let { user ->
        UserEditDialog(
            user = user,
            onDismiss = { editingUser = null },
            onConfirm = { updated ->
                viewModel.updateUser(updated)
                editingUser = null
            }
        )
    }
}

@Composable
fun UserCard(
    user: User,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // الصورة الرمزية
                Icon(
                    imageVector = if (user.role == "admin")
                        Icons.Default.AdminPanelSettings
                    else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (user.role == "admin")
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (user.role == "admin") Color(0xFFD32F2F)
                                else MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = if (user.role == "admin") "مدير النظام" else "مستخدم",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // حالة التفعيل
                Switch(
                    checked = user.isActive,
                    onCheckedChange = { onToggleActive() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تعديل")
                }

                TextButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete, null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun UserDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("user") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "الاسم الكامل",
                    modifier = Modifier.fillMaxWidth()
                )

                AppTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "اسم المستخدم",
                    modifier = Modifier.fillMaxWidth()
                )

                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "كلمة المرور",
                    modifier = Modifier.fillMaxWidth()
                )

                // اختيار الدور
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("الصلاحية: ")
                    RadioButton(
                        selected = role == "user",
                        onClick = { role = "user" }
                    )
                    Text("مستخدم")
                    RadioButton(
                        selected = role == "admin",
                        onClick = { role = "admin" }
                    )
                    Text("مدير")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, username, password, role) },
                enabled = name.isNotBlank() && username.isNotBlank() && password.isNotBlank()
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
fun UserEditDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (User) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل بيانات المستخدم") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "الاسم",
                    modifier = Modifier.fillMaxWidth()
                )

                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "كلمة مرور جديدة (اتركها فارغة للإبقاء)",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        user.copy(
                            name = name,
                            password = if (password.isNotBlank()) password else user.password
                        )
                    )
                }
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}