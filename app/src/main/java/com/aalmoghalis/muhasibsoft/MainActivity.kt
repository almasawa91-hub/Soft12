package com.aalmoghalis.muhasibsoft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aalmoghalis.muhasibsoft.presentation.screens.*
import com.aalmoghalis.muhasibsoft.presentation.theme.MuhasibSoftTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuhasibSoftTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MuhasibSoftApp()
                }
            }
        }
    }
}

@Composable
fun MuhasibSoftApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                onNavigateToSales = { navController.navigate("sales_list") },
                onNavigateToPurchases = { navController.navigate("purchases_list") },
                onNavigateToAccounts = { navController.navigate("accounts") },
                onNavigateToInventory = { navController.navigate("inventory") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        
        composable("sales_list") {
            SalesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddSale = { navController.navigate("add_sale") }
            )
        }
        
        composable("add_sale") {
            AddSaleScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("purchases_list") {
            PurchasesListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("accounts") {
            AccountsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("inventory") {
            InventoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}