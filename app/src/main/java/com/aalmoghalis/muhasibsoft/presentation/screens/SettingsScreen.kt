package com.aalmoghalis.muhasibsoft.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.presentation.components.AppTopBar
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "الإعدادات",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                SettingsItem(
                    title = "البيانات الشخصية",
                    icon = Icons.Default.Person,
                    onClick = { }
                )
            }

            item {
                SettingsItem(
                    title = "خيارات الطباعة",
                    icon = Icons.Default.Print,
                    onClick = { }
                )
            }

            item {
                SettingsItem(
                    title = "خيارات الأمان",
                    icon = Icons.Default.Security,
                    onClick = { }
                )
            }

            item {
                SettingsItem(
                    title = "المستخدمين والصلاحيات",
                    icon = Icons.Default.Group,
                    onClick = { }
                )
            }

            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

            item {
                SettingsItem(
                    title = "التصنيفات",
                    icon = Icons.Default.Category,
                    onClick = { }
                )
            }

            item {
                SettingsItem(
                    title = "مجموعة الصنف",
                    icon = Icons.Default.Inventory,
                    onClick = { }
                )
            }

            item {
                SettingsItem(
                    title = "وحدات القياس",
                    icon = Icons.Default.Straighten,
                    onClick = { }
                )
            }

            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

            item {
                SettingsItem(
                    title = "خيارات حفظ البيانات",
                    icon = Icons.Default.Save,
                    onClick = { }
                )
            }

            item {
                SettingsSwitchItem(
                    title = "الطابعة الحرارية",
                    icon = Icons.Default.Receipt,
                    checked = settings.thermalPrinter,
                    onCheckedChange = { viewModel.toggleThermalPrinter(it) }
                )
            }

            item {
                SettingsItem(
                    title = "الضريبة",
                    icon = Icons.Default.Percent,
                    subtitle = "${settings.taxRate}%",
                    onClick = { }
                )
            }

            item {
                SettingsItem(
                    title = "طابعة باركود الأصناف",
                    icon = Icons.Default.QrCode,
                    onClick = { }
                )
            }

            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

            item {
                SettingsSwitchItem(
                    title = "خيارات الإشعارات",
                    icon = Icons.Default.Notifications,
                    checked = settings.enableNotifications,
                    onCheckedChange = { viewModel.toggleNotifications(it) }
                )
            }

            item {
                SettingsSwitchItem(
                    title = "النسخ الاحتياطي التلقائي",
                    icon = Icons.Default.Backup,
                    checked = settings.autoBackup,
                    onCheckedChange = { viewModel.toggleAutoBackup(it) }
                )
            }

            item {
                SettingsItem(
                    title = "خيارات أخرى",
                    icon = Icons.Default.Tune,
                    onClick = { }
                )
            }

            item {
                SettingsItem(
                    title = "تفعيل الاشتراك",
                    icon = Icons.Default.CardMembership,
                    onClick = { }
                )
            }

            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

            item {
                SettingsItem(
                    title = "حول التطبيق",
                    icon = Icons.Default.Info,
                    subtitle = "الإصدار 1.0.0",
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    subtitle: String? = null
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}