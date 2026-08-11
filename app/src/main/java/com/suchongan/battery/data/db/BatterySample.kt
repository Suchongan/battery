package com.suchongan.battery.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "battery_samples", indices = [Index(value = ["timestampMillis"])])
data class BatterySample(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampMillis: Long,
    val level: Int,
    val temperatureC: Float,
    val voltageV: Float,
    val isCharging: Boolean,
)
