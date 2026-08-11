package com.suchongan.battery.data.battery

import com.suchongan.battery.core.BatteryHealth
import com.suchongan.battery.core.ChargingState
import com.suchongan.battery.core.PlugSource

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
) {
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
        )
    }
}
