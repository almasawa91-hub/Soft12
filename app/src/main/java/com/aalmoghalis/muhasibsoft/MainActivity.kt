package com.aalmoghalis.muhasibsoft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.presentation.theme.MuhasibSoftTheme
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuhasibSoftTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DashboardScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreen(viewModel: MainViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val numberFormat = NumberFormat.getNumberInstance(Locale("ar", "YE"))
    var showAddItem by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var cost by remember { mutableStateOf("0") }
    var price by remember { mutableStateOf("0") }
    var formError by remember { mutableStateOf<String?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("محاسب سوفت", fontWeight = FontWeight.Bold)
                        Text("نظام إدارة الحسابات والمخزون", style = MaterialTheme.typography.labelMedium)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddItem = true; formError = null }) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة صنف")
                    }
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "تحديث البيانات")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("لوحة المعلومات", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("ملخص البيانات المحفوظة على هذا الجهاز", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard("الأصناف", state.itemCount.toString(), Modifier.weight(1f)) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    MetricCard("فواتير البيع", state.saleCount.toString(), Modifier.weight(1f)) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Store, contentDescription = null)
                        Column {
                            Text("قيمة المخزون", style = MaterialTheme.typography.titleMedium)
                            Text(numberFormat.format(state.inventoryValue), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            if (state.isLoading) {
                item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            }
            state.error?.let { error ->
                item { Text("تعذر تحميل البيانات: $error", color = MaterialTheme.colorScheme.error) }
            }
            item { Text("الأصناف المسجلة", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            if (state.items.isEmpty() && !state.isLoading) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("لا توجد أصناف حتى الآن", style = MaterialTheme.typography.titleMedium)
                            Text("ستظهر الأصناف هنا بعد تسجيلها في قاعدة البيانات.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                items(state.items, key = { it.id }) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("الكمية: ${numberFormat.format(item.currentQuantity)}  •  سعر البيع: ${numberFormat.format(item.sellingPrice)}")
                            item.barcode?.takeIf { it.isNotBlank() }?.let { Text("الباركود: $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
    if (showAddItem) {
        AlertDialog(
            onDismissRequest = { showAddItem = false },
            title = { Text("إضافة صنف جديد") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("اسم الصنف *") }, singleLine = true)
                    OutlinedTextField(value = barcode, onValueChange = { barcode = it }, label = { Text("الباركود") }, singleLine = true)
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("الكمية الافتتاحية") }, singleLine = true)
                    OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("تكلفة الوحدة") }, singleLine = true)
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("سعر البيع") }, singleLine = true)
                    formError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val q = quantity.toDoubleOrNull()
                    val c = cost.toDoubleOrNull()
                    val p = price.toDoubleOrNull()
                    if (q == null || c == null || p == null) {
                        formError = "أدخل أرقاماً صحيحة للكمية والأسعار"
                    } else {
                        viewModel.addItem(itemName, barcode, q, c, p) { success, error ->
                            if (success) {
                                showAddItem = false
                                itemName = ""; barcode = ""; quantity = "0"; cost = "0"; price = "0"
                                formError = null
                            } else formError = error
                        }
                    }
                }) { Text("حفظ") }
            },
            dismissButton = { TextButton(onClick = { showAddItem = false }) { Text("إلغاء") } }
        )
    }

}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            icon()
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}
