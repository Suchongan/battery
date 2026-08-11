package com.suchongan.battery.core

/** Persisted debounce flags so alerts fire once per threshold-crossing, not on every broadcast. */
data class AlertState(
    val lowAlertFired: Boolean = false,
    val fullChargeAlertFired: Boolean = false,
)

data class AlertDecision(
    val fireLowBatteryAlert: Boolean,
    val fireFullChargeAlert: Boolean,
    val newState: AlertState,
)

/**
 * Pure decision logic for whether a low-battery or full-charge notification should fire,
 * given the current battery reading and the previously persisted debounce state. Kept
 * dependency-free so it's unit-testable without Android's `NotificationManager`.
 */
object AlertEvaluator {

    /** Percent above [lowBatteryThreshold] the level must recover to before re-arming the low-battery alert. */
    private const val LOW_BATTERY_RESET_HYSTERESIS = 5

    fun evaluate(
        levelPercent: Int,
        chargingState: ChargingState,
        lowBatteryThreshold: Int,
        notifyFullCharge: Boolean,
        notificationsEnabled: Boolean,
        previousState: AlertState,
    ): AlertDecision {
        if (!notificationsEnabled) {
            return AlertDecision(fireLowBatteryAlert = false, fireFullChargeAlert = false, newState = previousState)
        }

        val isChargingNow = chargingState == ChargingState.CHARGING || chargingState == ChargingState.FULL

        var lowFired = previousState.lowAlertFired
        var fireLow = false
        if (!isChargingNow && levelPercent <= lowBatteryThreshold) {
            if (!lowFired) {
                fireLow = true
                lowFired = true
            }
        } else if (isChargingNow || levelPercent > lowBatteryThreshold + LOW_BATTERY_RESET_HYSTERESIS) {
            lowFired = false
        }

        var fullFired = previousState.fullChargeAlertFired
        var fireFull = false
        val isFull = chargingState == ChargingState.FULL || (isChargingNow && levelPercent >= 100)
        if (notifyFullCharge && isFull) {
            if (!fullFired) {
                fireFull = true
                fullFired = true
            }
        } else if (!isChargingNow) {
            fullFired = false
        }

        return AlertDecision(
            fireLowBatteryAlert = fireLow,
            fireFullChargeAlert = fireFull,
            newState = AlertState(lowFired, fullFired),
        )
    }
}
