package com.aalmoghalis.muhasibsoft.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.data.model.Item
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.InventoryViewModel
import com.aalmoghalis.muhasibsoft.utils.BarcodeGenerator

/**
 * شاشة توليد وطباعة باركود الأصناف
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScreen(
    onNavigateBack: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var copiesPerRow by remember { mutableStateOf("1") }
    var totalCopies by remember { mutableStateOf("1") }

    val filteredItems = items.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "باركود الأصناف",
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
            // خيارات الطباعة
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "خيارات طباعة الباركود",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AppTextField(
                                value = copiesPerRow,
                                onValueChange = { copiesPerRow = it },
                                label = "عدد الباركود في الصف",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )

                            AppTextField(
                                value = totalCopies,
                                onValueChange = { totalCopies = it },
                                label = "العدد الكلي",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // البحث
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("بحث عن صنف") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // قائمة الأصناف مع الباركود
            items(filteredItems) { item ->
                BarcodeItemCard(item = item)
            }
        }
    }
}

@Composable
fun BarcodeItemCard(item: Item) {
    val barcodeContent = item.barcode ?: item.id.toString().padStart(12, '0')
    val bitmap = remember(barcodeContent) {
        BarcodeGenerator.generateBarcode(barcodeContent, 300, 80)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = String.format("%.2f", item.sellingPrice),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // صورة الباركود
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "باركود ${item.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = barcodeContent,
                style = MaterialTheme.typography.bodySmall,
                letterSpacing = androidx.compose.ui.unit.TextUnit(
                    2f,
                    androidx.compose.ui.unit.TextUnitType.Sp
                )
            )
        }
    }
}