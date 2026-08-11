package com.suchongan.battery.core

import kotlin.math.abs
import kotlin.math.ceil

/**
 * Estimates minutes remaining until full (charging) or empty (discharging) from the recent
 * rate of level change. Kept dependency-free (no Android) so it's unit-testable directly.
 */
object ChargeTimeEstimator {

    private const val MIN_WINDOW_MINUTES = 3.0

    /** Smallest level change (percent) treated as a real trend rather than sampling noise. */
    private const val MIN_LEVEL_DELTA = 1

    /**
     * @param recentSamples (timestampMillis, level) pairs from a short recent window, any order.
     * @return estimated whole minutes remaining, or null if there isn't enough recent data to
     *   compute a meaningful rate (too little elapsed time, level hasn't moved, or state is
     *   neither charging nor discharging).
     */
    fun estimateMinutesRemaining(
        chargingState: ChargingState,
        currentLevel: Int,
        recentSamples: List<Pair<Long, Int>>,
        nowMillis: Long,
    ): Int? {
        if (chargingState != ChargingState.CHARGING && chargingState != ChargingState.DISCHARGING) return null
        if (recentSamples.isEmpty()) return null

        val oldest = recentSamples.minBy { it.first }
        val elapsedMinutes = (nowMillis - oldest.first) / 60_000.0
        if (elapsedMinutes < MIN_WINDOW_MINUTES) return null

        val levelDelta = currentLevel - oldest.second
        if (abs(levelDelta) < MIN_LEVEL_DELTA) return null

        val ratePerMinute = levelDelta / elapsedMinutes
        if (chargingState == ChargingState.CHARGING && ratePerMinute <= 0) return null
        if (chargingState == ChargingState.DISCHARGING && ratePerMinute >= 0) return null

        val targetLevel = if (chargingState == ChargingState.CHARGING) 100 else 0
        val minutes = (targetLevel - currentLevel) / ratePerMinute
        if (!minutes.isFinite() || minutes < 0) return null
        return ceil(minutes).toInt()
    }
}
