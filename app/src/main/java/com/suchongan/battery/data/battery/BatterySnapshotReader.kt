package com.suchongan.battery.data.battery

import android.content.Intent
import android.os.BatteryManager
import com.suchongan.battery.core.BatteryStatusMapper

/**
 * Parses the extras Android attaches to `ACTION_BATTERY_CHANGED` (whether delivered live
 * or read back via a sticky `registerReceiver(null, filter)` call) into a [BatterySnapshot].
 */
object BatterySnapshotReader {

    fun fromIntent(intent: Intent?, nowMillis: () -> Long = System::currentTimeMillis): BatterySnapshot? {
        if (intent == null || !intent.hasExtra(BatteryManager.EXTRA_LEVEL)) return null

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY).orEmpty()

        return BatterySnapshot(
            levelPercent = BatteryStatusMapper.levelPercent(level, scale),
            chargingState = BatteryStatusMapper.mapChargingState(status),
            health = BatteryStatusMapper.mapHealth(health),
            plugSource = BatteryStatusMapper.mapPlugSource(plugged),
            temperatureCelsius = BatteryStatusMapper.temperatureCelsius(temperature),
            voltageVolts = BatteryStatusMapper.voltageVolts(voltage),
            technology = technology,
            timestampMillis = nowMillis(),
        )
    }
}
