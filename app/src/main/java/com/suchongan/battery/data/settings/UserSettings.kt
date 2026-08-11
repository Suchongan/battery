package com.suchongan.battery.data.settings

data class UserSettings(
    val lowBatteryThreshold: Int = 20,
    val notifyFullCharge: Boolean = true,
    val notificationsEnabled: Boolean = true,
)
