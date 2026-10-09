package com.aalmoghalis.muhasibsoft.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aalmoghalis.muhasibsoft.presentation.screens.*
import com.aalmoghalis.muhasibsoft.presentation.viewmodel.AuthViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MAIN = "main"
    const val SALES_LIST = "sales_list"
    const val ADD_SALE = "add_sale"
    const val PURCHASES_LIST = "purchases_list"
    const val ADD_PURCHASE = "add_purchase"
    const val ACCOUNTS = "accounts"
    const val JOURNAL = "journal"
    const val VOUCHER_RECEIPT = "voucher_receipt"
    const val VOUCHER_PAYMENT = "voucher_payment"
    const val INVENTORY = "inventory"
    const val CURRENCIES = "currencies"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // شاشة البداية
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // تسجيل الدخول
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // الشاشة الرئيسية
        composable(Routes.MAIN) {
            MainScreen(
                onNavigateToSales = { navController.navigate(Routes.SALES_LIST) },
                onNavigateToPurchases = { navController.navigate(Routes.PURCHASES_LIST) },
                onNavigateToAccounts = { navController.navigate(Routes.ACCOUNTS) },
                onNavigateToInventory = { navController.navigate(Routes.INVENTORY) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToVoucher = { type ->
                    if (type == "receipt") navController.navigate(Routes.VOUCHER_RECEIPT)
                    else navController.navigate(Routes.VOUCHER_PAYMENT)
                },
                onNavigateToCurrencies = { navController.navigate(Routes.CURRENCIES) },
                onNavigateToReports = { navController.navigate(Routes.REPORTS) }
            )
        }

        // المبيعات
        composable(Routes.SALES_LIST) {
            SalesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddSale = { navController.navigate(Routes.ADD_SALE) }
            )
        }

        composable(Routes.ADD_SALE) {
            AddSaleScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // المشتريات
        composable(Routes.PURCHASES_LIST) {
            PurchasesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddPurchase = { navController.navigate(Routes.ADD_PURCHASE) }
            )
        }

        composable(Routes.ADD_PURCHASE) {
            AddPurchaseScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // الحسابات والقيود
        composable(Routes.ACCOUNTS) {
            AccountsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToJournal = { navController.navigate(Routes.JOURNAL) }
            )
        }

        composable(Routes.JOURNAL) {
            JournalScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // السندات
        composable(Routes.VOUCHER_RECEIPT) {
            VoucherScreen(
                voucherType = "receipt",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.VOUCHER_PAYMENT) {
            VoucherScreen(
                voucherType = "payment",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // المخزون
        composable(Routes.INVENTORY) {
            InventoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // العملات
        composable(Routes.CURRENCIES) {
            CurrenciesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // التقارير
        composable(Routes.REPORTS) {
            ReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // الإعدادات
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}