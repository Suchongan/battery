package com.suchongan.battery.data.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * Dynamic-only receiver for battery events. `ACTION_BATTERY_CHANGED` has never supported
 * manifest-declared (implicit) registration, so this must be registered at runtime via
 * [Context.registerReceiver] — see [BatteryRepository.startCollecting].
 *
 * `ACTION_BATTERY_CHANGED` carries the battery extras directly. `ACTION_POWER_CONNECTED` /
 * `ACTION_POWER_DISCONNECTED` do not, so on those we do an immediate sticky re-read of
 * `ACTION_BATTERY_CHANGED` to capture the charging-state transition without waiting for
 * the next jittery broadcast.
 */
class BatteryBroadcastReceiver(
    private val onSnapshot: (BatterySnapshot) -> Unit,
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val snapshotIntent = if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            intent
        } else {
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }
        BatterySnapshotReader.fromIntent(snapshotIntent)?.let(onSnapshot)
    }
}
