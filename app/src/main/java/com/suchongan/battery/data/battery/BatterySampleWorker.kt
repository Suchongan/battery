package com.suchongan.battery.data.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.suchongan.battery.BatteryMonitorApp
import java.util.concurrent.TimeUnit

/**
 * Background safety net for the History screen: takes one sticky reading of
 * `ACTION_BATTERY_CHANGED` every ~15 minutes (WorkManager's minimum periodic interval) so
 * history keeps accumulating even if the app process isn't alive to receive live broadcasts.
 * Not a substitute for the live receiver — no foreground-service notification is used, so
 * OEM battery-management (MIUI/EMUI/etc.) can delay or skip individual runs.
 */
class BatterySampleWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sticky = applicationContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val snapshot = BatterySnapshotReader.fromIntent(sticky) ?: return Result.success()

        val repository = (applicationContext as BatteryMonitorApp).container.batteryRepository
        repository.handleWorkerSnapshot(snapshot)
        return Result.success()
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "battery_sample_worker"

        fun enqueuePeriodic(context: Context) {
            val request = PeriodicWorkRequestBuilder<BatterySampleWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
