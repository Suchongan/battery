package com.suchongan.battery.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.suchongan.battery.R

private const val CHANNEL_ID = "battery_alerts"
private const val LOW_BATTERY_NOTIFICATION_ID = 1001
private const val FULL_CHARGE_NOTIFICATION_ID = 1002

/**
 * Posts low-battery / full-charge notifications. Always checks
 * [NotificationManagerCompat.areNotificationsEnabled] before calling `notify()` — on API 33+
 * that covers both a missing runtime `POST_NOTIFICATIONS` permission and a user-disabled
 * channel, avoiding a `SecurityException`.
 */
class NotificationHelper(private val context: Context) {

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notification_channel_description)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun notifyLowBattery(levelPercent: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_low_battery_title))
            .setContentText(context.getString(R.string.notification_low_battery_text, levelPercent))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notify(LOW_BATTERY_NOTIFICATION_ID, notification)
    }

    fun notifyFullCharge() {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_full_charge_title))
            .setContentText(context.getString(R.string.notification_full_charge_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notify(FULL_CHARGE_NOTIFICATION_ID, notification)
    }

    private fun notify(id: Int, notification: Notification) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        NotificationManagerCompat.from(context).notify(id, notification)
    }
}
