package com.aalmoghalis.muhasibsoft.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.aalmoghalis.muhasibsoft.data.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class AppSettingsDataStore @Inject constructor(
    private val context: Context
) {
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings

    private object Keys {
        val COMPANY_NAME = stringPreferencesKey("company_name")
        val ADDRESS = stringPreferencesKey("address")
        val PHONE = stringPreferencesKey("phone")
        val TAX_NUMBER = stringPreferencesKey("tax_number")
        val CURRENCY = stringPreferencesKey("currency")
        val TAX_RATE = doublePreferencesKey("tax_rate")
        val INVOICE_PREFIX = stringPreferencesKey("invoice_prefix")
        val INVOICE_NUMBER = intPreferencesKey("invoice_number")
        val ENABLE_NOTIFICATIONS = booleanPreferencesKey("enable_notifications")
        val AUTO_BACKUP = booleanPreferencesKey("auto_backup")
        val THERMAL_PRINTER = booleanPreferencesKey("thermal_printer")
        val BARCODE_ENABLED = booleanPreferencesKey("barcode_enabled")
    }

    init {
        // تحميل الإعدادات عند البدء
        kotlinx.coroutines.GlobalScope.launch {
            context.dataStore.data
                .catch { e -> e.printStackTrace() }
                .collect { prefs ->
                    _settings.value = AppSettings(
                        companyName = prefs[Keys.COMPANY_NAME] ?: "",
                        address = prefs[Keys.ADDRESS] ?: "",
                        phone = prefs[Keys.PHONE] ?: "",
                        taxNumber = prefs[Keys.TAX_NUMBER] ?: "",
                        currency = prefs[Keys.CURRENCY] ?: "local",
                        taxRate = prefs[Keys.TAX_RATE] ?: 0.0,
                        invoicePrefix = prefs[Keys.INVOICE_PREFIX] ?: "",
                        invoiceNumber = prefs[Keys.INVOICE_NUMBER] ?: 1,
                        enableNotifications = prefs[Keys.ENABLE_NOTIFICATIONS] ?: true,
                        autoBackup = prefs[Keys.AUTO_BACKUP] ?: false,
                        thermalPrinter = prefs[Keys.THERMAL_PRINTER] ?: false,
                        barcodeEnabled = prefs[Keys.BARCODE_ENABLED] ?: false
                    )
                }
        }
    }

    suspend fun updateCompanyName(name: String) {
        context.dataStore.edit { it[Keys.COMPANY_NAME] = name }
    }

    suspend fun updateAddress(address: String) {
        context.dataStore.edit { it[Keys.ADDRESS] = address }
    }

    suspend fun updatePhone(phone: String) {
        context.dataStore.edit { it[Keys.PHONE] = phone }
    }

    suspend fun updateTaxRate(rate: Double) {
        context.dataStore.edit { it[Keys.TAX_RATE] = rate }
    }

    suspend fun updateNotifications(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ENABLE_NOTIFICATIONS] = enabled }
    }

    suspend fun updateAutoBackup(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_BACKUP] = enabled }
    }

    suspend fun updateThermalPrinter(enabled: Boolean) {
        context.dataStore.edit { it[Keys.THERMAL_PRINTER] = enabled }
    }

    suspend fun updateBarcodeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BARCODE_ENABLED] = enabled }
    }

    suspend fun incrementInvoiceNumber() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.INVOICE_NUMBER] ?: 1
            prefs[Keys.INVOICE_NUMBER] = current + 1
        }
    }
}