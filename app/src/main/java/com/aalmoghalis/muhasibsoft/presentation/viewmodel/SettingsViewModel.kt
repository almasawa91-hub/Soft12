package com.aalmoghalis.muhasibsoft.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aalmoghalis.muhasibsoft.data.local.AppSettingsDataStore
import com.aalmoghalis.muhasibsoft.data.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: AppSettingsDataStore
) : ViewModel() {

    val settings: StateFlow<AppSettings> = dataStore.settings

    fun updateCompanyName(name: String) {
        viewModelScope.launch { dataStore.updateCompanyName(name) }
    }

    fun updateTaxRate(rate: Double) {
        viewModelScope.launch { dataStore.updateTaxRate(rate) }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch { dataStore.updateNotifications(enabled) }
    }

    fun toggleThermalPrinter(enabled: Boolean) {
        viewModelScope.launch { dataStore.updateThermalPrinter(enabled) }
    }

    fun toggleAutoBackup(enabled: Boolean) {
        viewModelScope.launch { dataStore.updateAutoBackup(enabled) }
    }
}