package com.suchongan.battery.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChargeTimeEstimatorTest {

    @Test
    fun `estimates minutes to full while charging`() {
        // 10 minutes ago the level was 50%, now it's 55% -> 0.5%/min -> 45% remaining / 0.5 = 90 min
        val result = ChargeTimeEstimator.estimateMinutesRemaining(
            chargingState = ChargingState.CHARGING,
            currentLevel = 55,
            recentSamples = listOf(0L to 50),
            nowMillis = 10 * 60_000L,
        )
        assertEquals(90, result)
    }

    @Test
    fun `estimates minutes to empty while discharging`() {
        // 20 minutes ago the level was 80%, now it's 78% -> -0.1%/min -> 78% / 0.1 = 780 min
        val result = ChargeTimeEstimator.estimateMinutesRemaining(
            chargingState = ChargingState.DISCHARGING,
            currentLevel = 78,
            recentSamples = listOf(0L to 80),
            nowMillis = 20 * 60_000L,
        )
        assertEquals(780, result)
    }

    @Test
    fun `returns null when window is too short`() {
        val result = ChargeTimeEstimator.estimateMinutesRemaining(
            chargingState = ChargingState.CHARGING,
            currentLevel = 55,
            recentSamples = listOf(0L to 50),
            nowMillis = 60_000L, // only 1 minute elapsed
        )
        assertNull(result)
    }

    @Test
    fun `returns null when level hasn't moved`() {
        val result = ChargeTimeEstimator.estimateMinutesRemaining(
            chargingState = ChargingState.CHARGING,
            currentLevel = 50,
            recentSamples = listOf(0L to 50),
            nowMillis = 10 * 60_000L,
        )
        assertNull(result)
    }

    @Test
    fun `returns null when charging but level is dropping`() {
        val result = ChargeTimeEstimator.estimateMinutesRemaining(
            chargingState = ChargingState.CHARGING,
            currentLevel = 45,
            recentSamples = listOf(0L to 50),
            nowMillis = 10 * 60_000L,
        )
        assertNull(result)
    }

    @Test
    fun `returns null for full or unknown state`() {
        assertNull(
            ChargeTimeEstimator.estimateMinutesRemaining(
                chargingState = ChargingState.FULL,
                currentLevel = 100,
                recentSamples = listOf(0L to 95),
                nowMillis = 10 * 60_000L,
            ),
        )
        assertNull(
            ChargeTimeEstimator.estimateMinutesRemaining(
                chargingState = ChargingState.UNKNOWN,
                currentLevel = 50,
                recentSamples = listOf(0L to 45),
                nowMillis = 10 * 60_000L,
            ),
        )
    }

    @Test
    fun `returns null with fewer than two samples`() {
        assertNull(
            ChargeTimeEstimator.estimateMinutesRemaining(
                chargingState = ChargingState.CHARGING,
                currentLevel = 55,
                recentSamples = emptyList(),
                nowMillis = 10 * 60_000L,
            ),
        )
    }

    @Test
    fun `uses the oldest sample in an unsorted window`() {
        val result = ChargeTimeEstimator.estimateMinutesRemaining(
            chargingState = ChargingState.CHARGING,
            currentLevel = 55,
            recentSamples = listOf(5 * 60_000L to 52, 0L to 50),
            nowMillis = 10 * 60_000L,
        )
        assertEquals(90, result)
    }
}
