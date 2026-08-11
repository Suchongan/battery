package com.suchongan.battery.ui.history

import com.suchongan.battery.R

enum class HistoryRange(val durationMillis: Long, val labelResId: Int) {
    ONE_HOUR(60 * 60 * 1000L, R.string.history_range_1h),
    TWENTY_FOUR_HOURS(24 * 60 * 60 * 1000L, R.string.history_range_24h),
    SEVEN_DAYS(7 * 24 * 60 * 60 * 1000L, R.string.history_range_7d),
}
