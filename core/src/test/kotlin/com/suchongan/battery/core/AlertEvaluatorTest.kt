package com.suchongan.battery.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertEvaluatorTest {

    @Test
    fun `fires low battery alert once when crossing threshold while discharging`() {
        val first = AlertEvaluator.evaluate(
            levelPercent = 15,
            chargingState = ChargingState.DISCHARGING,
            lowBatteryThreshold = 20,
            notifyFullCharge = true,
            notificationsEnabled = true,
            previousState = AlertState(),
        )
        assertTrue(first.fireLowBatteryAlert)
        assertTrue(first.newState.lowAlertFired)

        // A second reading still under threshold must not re-fire.
        val second = AlertEvaluator.evaluate(
            levelPercent = 14,
            chargingState = ChargingState.DISCHARGING,
            lowBatteryThreshold = 20,
            notifyFullCharge = true,
            notificationsEnabled = true,
            previousState = first.newState,
        )
        assertFalse(second.fireLowBatteryAlert)
    }

    @Test
    fun `resets low battery alert once level recovers past hysteresis`() {
        val fired = AlertState(lowAlertFired = true)
        val recovered = AlertEvaluator.evaluate(
            levelPercent = 30,
            chargingState = ChargingState.DISCHARGING,
            lowBatteryThreshold = 20,
            notifyFullCharge = true,
            notificationsEnabled = true,
            previousState = fired,
        )
        assertFalse(recovered.fireLowBatteryAlert)
        assertFalse(recovered.newState.lowAlertFired)
    }

    @Test
    fun `does not fire low battery alert while charging`() {
        val result = AlertEvaluator.evaluate(
            levelPercent = 10,
            chargingState = ChargingState.CHARGING,
            lowBatteryThreshold = 20,
            notifyFullCharge = true,
            notificationsEnabled = true,
            previousState = AlertState(),
        )
        assertFalse(result.fireLowBatteryAlert)
    }

    @Test
    fun `fires full charge alert once when reaching full`() {
        val first = AlertEvaluator.evaluate(
            levelPercent = 100,
            chargingState = ChargingState.FULL,
            lowBatteryThreshold = 20,
            notifyFullCharge = true,
            notificationsEnabled = true,
            previousState = AlertState(),
        )
        assertTrue(first.fireFullChargeAlert)

        val second = AlertEvaluator.evaluate(
            levelPercent = 100,
            chargingState = ChargingState.FULL,
            lowBatteryThreshold = 20,
            notifyFullCharge = true,
            notificationsEnabled = true,
            previousState = first.newState,
        )
        assertFalse(second.fireFullChargeAlert)
    }

    @Test
    fun `does not fire full charge alert when disabled in settings`() {
        val result = AlertEvaluator.evaluate(
            levelPercent = 100,
            chargingState = ChargingState.FULL,
            lowBatteryThreshold = 20,
            notifyFullCharge = false,
            notificationsEnabled = true,
            previousState = AlertState(),
        )
        assertFalse(result.fireFullChargeAlert)
    }

    @Test
    fun `master notifications toggle suppresses all alerts`() {
        val result = AlertEvaluator.evaluate(
            levelPercent = 5,
            chargingState = ChargingState.DISCHARGING,
            lowBatteryThreshold = 20,
            notifyFullCharge = true,
            notificationsEnabled = false,
            previousState = AlertState(),
        )
        assertFalse(result.fireLowBatteryAlert)
        assertFalse(result.fireFullChargeAlert)
        assertEquals(AlertState(), result.newState)
    }

    @Test
    fun `full charge alert resets after unplugging`() {
        val fired = AlertState(fullChargeAlertFired = true)
        val unplugged = AlertEvaluator.evaluate(
            levelPercent = 95,
            chargingState = ChargingState.DISCHARGING,
            lowBatteryThreshold = 20,
            notifyFullCharge = true,
            notificationsEnabled = true,
            previousState = fired,
        )
        assertFalse(unplugged.newState.fullChargeAlertFired)
    }
}
