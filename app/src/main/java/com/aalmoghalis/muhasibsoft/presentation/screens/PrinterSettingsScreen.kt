package com.aalmoghalis.muhasibsoft.presentation.screens

import android.bluetooth.BluetoothDevice
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
import androidx.compose.ui.unit.dp
import com.aalmoghalis.muhasibsoft.presentation.components.AppTopBar
import com.aalmoghalis.muhasibsoft.utils.ThermalPrinter
import kotlinx.coroutines.launch

/**
 * شاشة إعدادات الطابعة الحرارية والاتصال بها
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterSettingsScreen(onNavigateBack: () -> Unit) {
    val printer = remember { ThermalPrinter() }
    val scope = rememberCoroutineScope()

    var pairedDevices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
    var connectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isConnecting by remember { mutableStateOf(false) }

    // تحميل الأجهزة المقترنة
    LaunchedEffect(Unit) {
        pairedDevices = printer.getPairedDevices()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "الطابعة الحرارية",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // حالة الاتصال
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (connectedDevice != null)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (connectedDevice != null)
                                Icons.Default.Print else Icons.Default.PrintDisabled,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (connectedDevice != null)
                                    "متصل: ${connectedDevice?.name}"
                                else "غير متصل",
                                style = MaterialTheme.typography.titleMedium
                            )
                            statusMessage?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // الأجهزة المتاحة
            item {
                Text(
                    text = "الأجهزة المقترنة:",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (pairedDevices.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.BluetoothDisabled, null,
                                modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("لا توجد أجهزة Bluetooth مقترنة")
                            Text(
                                text = "قم بإقران الطابعة من إعدادات النظام أولاً",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                items(pairedDevices) { device ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    isConnecting = true
                                    statusMessage = "جاري الاتصال..."
                                    printer.connect(device)
                                        .onSuccess {
                                            connectedDevice = device
                                            statusMessage = "تم الاتصال بنجاح"
                                        }
                                        .onFailure {
                                            statusMessage = "فشل الاتصال: ${it.message}"
                                        }
                                    isConnecting = false
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Bluetooth, null,
                                tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.name ?: "جهاز بدون اسم",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = device.address,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (isConnecting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            } else if (connectedDevice?.address == device.address) {
                                Icon(Icons.Default.CheckCircle, null,
                                    tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }

            // زر طباعة اختبار
            item {
                Button(
                    onClick = {
                        scope.launch {
                            printer.printTest()
                                .onSuccess { statusMessage = "تم إرسال صفحة الاختبار" }
                                .onFailure { statusMessage = "فشل: ${it.message}" }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = connectedDevice != null
                ) {
                    Icon(Icons.Default.Print, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("طباعة صفحة اختبار")
                }
            }

            // زر قطع الاتصال
            if (connectedDevice != null) {
                item {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                printer.disconnect()
                                connectedDevice = null
                                statusMessage = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("قطع الاتصال")
                    }
                }
            }
        }
    }
}