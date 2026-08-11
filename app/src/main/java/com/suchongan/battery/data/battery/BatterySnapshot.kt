package com.suchongan.battery.data.battery

import com.suchongan.battery.core.BatteryHealth
import com.suchongan.battery.core.ChargingState
import com.suchongan.battery.core.PlugSource
import kotlin.math.abs

/** A single point-in-time reading of the device's battery state. */
data class BatterySnapshot(
    val levelPercent: Int,
    val chargingState: ChargingState,
    val health: BatteryHealth,
    val plugSource: PlugSource,
    val temperatureCelsius: Float,
    val voltageVolts: Float,
    val technology: String,
    val timestampMillis: Long,
    /** Instantaneous current in mA, or null if the device doesn't report BATTERY_PROPERTY_CURRENT_NOW. */
    val currentMilliAmps: Int?,
) {
    /**
     * Current's charge/discharge sign isn't consistently reported across OEMs, so this uses the
     * magnitude only — direction is already conveyed by [chargingState].
     */
    val powerWatts: Float?
        get() = currentMilliAmps?.let { abs(it) / 1000f * voltageVolts }

    companion object {
        val UNKNOWN = BatterySnapshot(
            levelPercent = 0,
            chargingState = ChargingState.UNKNOWN,
            health = BatteryHealth.UNKNOWN,
            plugSource = PlugSource.UNKNOWN,
            temperatureCelsius = 0f,
            voltageVolts = 0f,
            technology = "",
            timestampMillis = 0L,
            currentMilliAmps = null,
        )
    }
}
