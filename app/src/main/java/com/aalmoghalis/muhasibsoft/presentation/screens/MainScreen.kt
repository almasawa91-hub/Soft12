package com.aalmoghalis.muhasibsoft.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aalmoghalis.muhasibsoft.presentation.components.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSales: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDrawer by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = "محاسب سوفت",
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, "الإشعارات")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, "مشاركة")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // الأزرار الرئيسية
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    MainActionButton(
                        title = "قبض/صرف",
                        icon = Icons.Default.AttachMoney,
                        onClick = { }
                    )
                }
                
                item {
                    MainActionButton(
                        title = "المبيعات",
                        icon = Icons.Default.ShoppingCart,
                        onClick = onNavigateToSales
                    )
                }
                
                item {
                    MainActionButton(
                        title = "الحسابات",
                        icon = Icons.Default.Person,
                        onClick = onNavigateToAccounts
                    )
                }
                
                item {
                    MainActionButton(
                        title = "المشتريات",
                        icon = Icons.Default.ShoppingCart,
                        onClick = onNavigateToPurchases
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // القوائم القابلة للطي
            ExpandableSection(
                title = "عمليات مخزنية",
                items = listOf("صرف مخزني", "توريد مخزني", "تحويل مخزني", "تسوية مخزنية", "إضافة مخزن", "جرد مخزني"),
                onItemClick = { onNavigateToInventory() }
            )
            
            ExpandableSection(
                title = "قيود وحسابات",
                items = listOf("القيود اليومية", "إضافة حساب"),
                onItemClick = { onNavigateToAccounts() }
            )
            
            ExpandableSection(
                title = "التقارير",
                items = listOf("المخزون المتبقي", "أرصدة الحسابات", "أرباح الأصناف"),
                onItemClick = { }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // الإعدادات
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("الإعدادات")
            }
        }
    }
}

@Composable
fun MainActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    items: List<String>,
    onItemClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }
            
            if (expanded) {
                Divider()
                items.forEach { item ->
                    TextButton(
                        onClick = onItemClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
}