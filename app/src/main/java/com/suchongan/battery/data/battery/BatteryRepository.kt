package com.suchongan.battery.data.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.suchongan.battery.core.AlertEvaluator
import com.suchongan.battery.core.ChargingState
import com.suchongan.battery.data.db.BatterySample
import com.suchongan.battery.data.db.BatterySampleDao
import com.suchongan.battery.data.settings.SettingsRepository
import com.suchongan.battery.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** How often a live-received reading is allowed to write a new history row. */
private const val SAMPLE_MIN_INTERVAL_MS = 5 * 60 * 1000L

/** How long history rows are kept before being pruned. */
private const val RETENTION_MS = 30L * 24 * 60 * 60 * 1000L

/**
 * Single source of truth for battery state: exposes the live [snapshot] to the UI, persists
 * throttled samples to Room for the History screen, and evaluates/fires alerts. Shared between
 * the live [BatteryBroadcastReceiver] (registered in the Application) and [BatterySampleWorker]
 * (the WorkManager background safety net), so both paths go through the same alert-debounce
 * and persistence logic.
 */
class BatteryRepository(
    private val batterySampleDao: BatterySampleDao,
    private val settingsRepository: SettingsRepository,
    private val notificationHelper: NotificationHelper,
    private val externalScope: CoroutineScope,
) {
    private val _snapshot = MutableStateFlow(BatterySnapshot.UNKNOWN)
    val snapshot: StateFlow<BatterySnapshot> = _snapshot.asStateFlow()

    private val receiver = BatteryBroadcastReceiver(::onLiveSnapshot)

    private var lastPersistedLevel: Int? = null
    private var lastPersistedAtMillis: Long = 0L

    /** Registers the dynamic receiver and seeds [snapshot] from the current sticky battery intent. */
    fun startCollecting(context: Context) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        context.registerReceiver(receiver, filter)

        val sticky = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        BatterySnapshotReader.fromIntent(sticky)?.let { _snapshot.value = it }
    }

    fun historyFlow(sinceMillis: Long): Flow<List<BatterySample>> = batterySampleDao.getSamplesSince(sinceMillis)

    private fun onLiveSnapshot(snap: BatterySnapshot) {
        _snapshot.value = snap
        externalScope.launch { handleSnapshot(snap, forcePersist = false) }
    }

    /** Called by [BatterySampleWorker] with a freshly-read sticky snapshot; always persisted. */
    suspend fun handleWorkerSnapshot(snap: BatterySnapshot) {
        handleSnapshot(snap, forcePersist = true)
    }

    private suspend fun handleSnapshot(snap: BatterySnapshot, forcePersist: Boolean) {
        if (forcePersist) {
            persist(snap)
        } else {
            maybePersist(snap)
        }
        evaluateAlerts(snap)
    }

    private suspend fun maybePersist(snap: BatterySnapshot) {
        val levelChanged = snap.levelPercent != lastPersistedLevel
        val intervalElapsed = snap.timestampMillis - lastPersistedAtMillis >= SAMPLE_MIN_INTERVAL_MS
        if (levelChanged || intervalElapsed) {
            persist(snap)
        }
    }

    private suspend fun persist(snap: BatterySnapshot) {
        batterySampleDao.insert(
            BatterySample(
                timestampMillis = snap.timestampMillis,
                level = snap.levelPercent,
                temperatureC = snap.temperatureCelsius,
                voltageV = snap.voltageVolts,
                isCharging = snap.chargingState == ChargingState.CHARGING ||
                    snap.chargingState == ChargingState.FULL,
            ),
        )
        lastPersistedLevel = snap.levelPercent
        lastPersistedAtMillis = snap.timestampMillis
        batterySampleDao.deleteOlderThan(snap.timestampMillis - RETENTION_MS)
    }

    private suspend fun evaluateAlerts(snap: BatterySnapshot) {
        val settings = settingsRepository.settingsFlow.first()
        if (!settings.notificationsEnabled) return

        val previousAlertState = settingsRepository.alertStateFlow.first()
        val decision = AlertEvaluator.evaluate(
            levelPercent = snap.levelPercent,
            chargingState = snap.chargingState,
            lowBatteryThreshold = settings.lowBatteryThreshold,
            notifyFullCharge = settings.notifyFullCharge,
            notificationsEnabled = settings.notificationsEnabled,
            previousState = previousAlertState,
        )
        if (decision.newState != previousAlertState) {
            settingsRepository.updateAlertState(decision.newState)
        }
        if (decision.fireLowBatteryAlert) {
            notificationHelper.notifyLowBattery(snap.levelPercent)
        }
        if (decision.fireFullChargeAlert) {
            notificationHelper.notifyFullCharge()
        }
    }
}
