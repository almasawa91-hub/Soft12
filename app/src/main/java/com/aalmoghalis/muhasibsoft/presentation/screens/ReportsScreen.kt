package com.aalmoghalis.muhasibsoft.presentation.screens

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

data class ReportItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(onNavigateBack: () -> Unit) {
    val reports = listOf(
        ReportItem("المخزون المتبقي", Icons.Default.Inventory),
        ReportItem("أرصدة الحسابات", Icons.Default.AccountBalance),
        ReportItem("أرباح الأصناف", Icons.Default.TrendingUp),
        ReportItem("العمليات اليومية", Icons.Default.Today),
        ReportItem("القيود اليومية", Icons.Default.Receipt),
        ReportItem("حركة الصندوق", Icons.Default.Payments),
        ReportItem("حركة الحسابات", Icons.Default.SwapVert),
        ReportItem("إجمالي التصنيفات", Icons.Default.Category),
        ReportItem("فوارق أسعار العملات", Icons.Default.CurrencyExchange),
        ReportItem("رأس المال العامل", Icons.Default.Work),
        ReportItem("الربح على مستوى العميل", Icons.Default.Person),
        ReportItem("المبيعات حسب الصنف", Icons.Default.ShoppingCart),
        ReportItem("المشتريات حسب الصنف", Icons.Default.LocalShipping),
        ReportItem("تفاصيل حركة الأصناف", Icons.Default.List)
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "التقارير",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reports) { report ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { },
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = report.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = report.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}