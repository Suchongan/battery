package com.suchongan.battery.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.suchongan.battery.core.AlertState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val LOW_BATTERY_THRESHOLD = intPreferencesKey("low_battery_threshold")
        val NOTIFY_FULL_CHARGE = booleanPreferencesKey("notify_full_charge")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LOW_ALERT_FIRED = booleanPreferencesKey("low_alert_fired")
        val FULL_ALERT_FIRED = booleanPreferencesKey("full_alert_fired")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            lowBatteryThreshold = prefs[Keys.LOW_BATTERY_THRESHOLD] ?: 20,
            notifyFullCharge = prefs[Keys.NOTIFY_FULL_CHARGE] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
        )
    }

    val alertStateFlow: Flow<AlertState> = context.dataStore.data.map { prefs ->
        AlertState(
            lowAlertFired = prefs[Keys.LOW_ALERT_FIRED] ?: false,
            fullChargeAlertFired = prefs[Keys.FULL_ALERT_FIRED] ?: false,
        )
    }

    suspend fun setLowBatteryThreshold(percent: Int) {
        context.dataStore.edit { it[Keys.LOW_BATTERY_THRESHOLD] = percent.coerceIn(1, 99) }
    }

    suspend fun setNotifyFullCharge(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFY_FULL_CHARGE] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun updateAlertState(state: AlertState) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LOW_ALERT_FIRED] = state.lowAlertFired
            prefs[Keys.FULL_ALERT_FIRED] = state.fullChargeAlertFired
        }
    }
}
