package com.suchongan.battery.core

/** Charging state derived from `BatteryManager.EXTRA_STATUS`. */
enum class ChargingState { CHARGING, DISCHARGING, FULL, NOT_CHARGING, UNKNOWN }

/** Battery health derived from `BatteryManager.EXTRA_HEALTH`. */
enum class BatteryHealth { GOOD, OVERHEAT, DEAD, OVER_VOLTAGE, UNSPECIFIED_FAILURE, COLD, UNKNOWN }

/** Power source derived from `BatteryManager.EXTRA_PLUGGED`. */
enum class PlugSource { AC, USB, WIRELESS, DOCK, NONE, UNKNOWN }

/**
 * Pure mapping from the raw int/extra values Android's `ACTION_BATTERY_CHANGED` intent
 * carries to domain types. Kept dependency-free (no `android.jar`) so it's unit-testable
 * in a plain JVM module. The raw integer values mirrored below match the stable, documented
 * `BatteryManager` constants and have not changed across Android versions.
 */
object BatteryStatusMapper {

    // Mirrors android.os.BatteryManager.BATTERY_STATUS_*
    private const val STATUS_UNKNOWN = 1
    private const val STATUS_CHARGING = 2
    private const val STATUS_DISCHARGING = 3
    private const val STATUS_NOT_CHARGING = 4
    private const val STATUS_FULL = 5

    // Mirrors android.os.BatteryManager.BATTERY_HEALTH_*
    private const val HEALTH_UNKNOWN = 1
    private const val HEALTH_GOOD = 2
    private const val HEALTH_OVERHEAT = 3
    private const val HEALTH_DEAD = 4
    private const val HEALTH_OVER_VOLTAGE = 5
    private const val HEALTH_UNSPECIFIED_FAILURE = 6
    private const val HEALTH_COLD = 7

    // Mirrors android.os.BatteryManager.BATTERY_PLUGGED_*
    private const val PLUGGED_AC = 1
    private const val PLUGGED_USB = 2
    private const val PLUGGED_WIRELESS = 4
    private const val PLUGGED_DOCK = 8

    fun mapChargingState(statusExtra: Int): ChargingState = when (statusExtra) {
        STATUS_CHARGING -> ChargingState.CHARGING
        STATUS_DISCHARGING -> ChargingState.DISCHARGING
        STATUS_FULL -> ChargingState.FULL
        STATUS_NOT_CHARGING -> ChargingState.NOT_CHARGING
        STATUS_UNKNOWN -> ChargingState.UNKNOWN
        else -> ChargingState.UNKNOWN
    }

    fun mapHealth(healthExtra: Int): BatteryHealth = when (healthExtra) {
        HEALTH_GOOD -> BatteryHealth.GOOD
        HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
        HEALTH_DEAD -> BatteryHealth.DEAD
        HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
        HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.UNSPECIFIED_FAILURE
        HEALTH_COLD -> BatteryHealth.COLD
        HEALTH_UNKNOWN -> BatteryHealth.UNKNOWN
        else -> BatteryHealth.UNKNOWN
    }

    fun mapPlugSource(pluggedExtra: Int): PlugSource = when (pluggedExtra) {
        0 -> PlugSource.NONE
        PLUGGED_AC -> PlugSource.AC
        PLUGGED_USB -> PlugSource.USB
        PLUGGED_WIRELESS -> PlugSource.WIRELESS
        PLUGGED_DOCK -> PlugSource.DOCK
        else -> PlugSource.UNKNOWN
    }

    /** `EXTRA_LEVEL` / `EXTRA_SCALE` -> whole-number percent (0-100). */
    fun levelPercent(level: Int, scale: Int): Int {
        if (scale <= 0) return 0
        return ((level.toFloat() / scale.toFloat()) * 100f).toInt().coerceIn(0, 100)
    }

    /** `EXTRA_TEMPERATURE` is tenths of a degree Celsius. */
    fun temperatureCelsius(rawTemperature: Int): Float = rawTemperature / 10f

    /** `EXTRA_VOLTAGE` is millivolts. */
    fun voltageVolts(rawVoltageMillivolts: Int): Float = rawVoltageMillivolts / 1000f
}
