package com.suchongan.battery.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suchongan.battery.data.settings.SettingsRepository
import com.suchongan.battery.data.settings.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val settings: StateFlow<UserSettings> = settingsRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    fun setLowBatteryThreshold(percent: Int) {
        viewModelScope.launch { settingsRepository.setLowBatteryThreshold(percent) }
    }

    fun setNotifyFullCharge(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotifyFullCharge(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationsEnabled(enabled) }
    }
}
