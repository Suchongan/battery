package com.suchongan.battery.di

import android.content.Context
import com.suchongan.battery.data.battery.BatteryRepository
import com.suchongan.battery.data.db.BatteryDatabase
import com.suchongan.battery.data.settings.SettingsRepository
import com.suchongan.battery.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Small manual dependency container — no Hilt/Dagger. Built once in [BatteryMonitorApp] and
 * handed to ViewModels via [com.suchongan.battery.ui.ViewModelFactory].
 */
class AppContainer(context: Context) {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val settingsRepository = SettingsRepository(context)
    val notificationHelper = NotificationHelper(context)

    private val database = BatteryDatabase.getInstance(context)

    val batteryRepository = BatteryRepository(
        batterySampleDao = database.batterySampleDao(),
        settingsRepository = settingsRepository,
        notificationHelper = notificationHelper,
        externalScope = applicationScope,
    )
}
