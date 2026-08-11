package com.suchongan.battery.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suchongan.battery.core.ChargeTimeEstimator
import com.suchongan.battery.data.battery.BatteryRepository
import com.suchongan.battery.data.battery.BatterySnapshot
import com.suchongan.battery.data.db.BatterySample
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Window used to compute the charge/discharge rate for the time-remaining estimate. */
private const val ESTIMATE_WINDOW_MS = 30 * 60 * 1000L

/** Window shown in the Dashboard's inline trend chart. */
private const val CHART_WINDOW_MS = 24 * 60 * 60 * 1000L

class DashboardViewModel(repository: BatteryRepository) : ViewModel() {

    val snapshot: StateFlow<BatterySnapshot> = repository.snapshot

    val recentHistory: StateFlow<List<BatterySample>> =
        repository.historyFlow(System.currentTimeMillis() - CHART_WINDOW_MS)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val estimatedMinutesRemaining: StateFlow<Int?> = combine(
        snapshot,
        repository.historyFlow(System.currentTimeMillis() - ESTIMATE_WINDOW_MS),
    ) { snap, samples ->
        ChargeTimeEstimator.estimateMinutesRemaining(
            chargingState = snap.chargingState,
            currentLevel = snap.levelPercent,
            recentSamples = samples.map { it.timestampMillis to it.level },
            nowMillis = snap.timestampMillis,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
