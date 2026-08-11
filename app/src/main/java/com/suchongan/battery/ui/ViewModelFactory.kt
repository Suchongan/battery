package com.suchongan.battery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suchongan.battery.di.AppContainer
import com.suchongan.battery.ui.dashboard.DashboardViewModel
import com.suchongan.battery.ui.history.HistoryViewModel
import com.suchongan.battery.ui.settings.SettingsViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
            DashboardViewModel(container.batteryRepository) as T

        modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
            HistoryViewModel(container.batteryRepository) as T

        modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
            SettingsViewModel(container.settingsRepository) as T

        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
