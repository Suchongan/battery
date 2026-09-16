package com.suchongan.battery.core

/**
 * Coarse charging-speed tier inferred from delivered power. Android doesn't expose the
 * actually-negotiated protocol (USB PD, Quick Charge, etc.) without root, so this buckets
 * observed wattage into tiers roughly matching common charger classes.
 */
enum class ChargingProtocol { NONE, UNKNOWN, STANDARD, FAST, SUPER_FAST }

object ChargingProtocolClassifier {
    private const val STANDARD_MAX_WATTS = 7.5f
    private const val FAST_MAX_WATTS = 18f

    /** [powerWatts] is null when the device doesn't report charging current. */
    fun classify(isCharging: Boolean, powerWatts: Float?): ChargingProtocol = when {
        !isCharging -> ChargingProtocol.NONE
        powerWatts == null -> ChargingProtocol.UNKNOWN
        powerWatts < STANDARD_MAX_WATTS -> ChargingProtocol.STANDARD
        powerWatts < FAST_MAX_WATTS -> ChargingProtocol.FAST
        else -> ChargingProtocol.SUPER_FAST
    }
}
