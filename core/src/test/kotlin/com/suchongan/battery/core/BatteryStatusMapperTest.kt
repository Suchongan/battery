package com.suchongan.battery.core

import org.junit.Assert.assertEquals
import org.junit.Test

class BatteryStatusMapperTest {

    @Test
    fun `maps charging states`() {
        assertEquals(ChargingState.CHARGING, BatteryStatusMapper.mapChargingState(2))
        assertEquals(ChargingState.DISCHARGING, BatteryStatusMapper.mapChargingState(3))
        assertEquals(ChargingState.NOT_CHARGING, BatteryStatusMapper.mapChargingState(4))
        assertEquals(ChargingState.FULL, BatteryStatusMapper.mapChargingState(5))
        assertEquals(ChargingState.UNKNOWN, BatteryStatusMapper.mapChargingState(1))
        assertEquals(ChargingState.UNKNOWN, BatteryStatusMapper.mapChargingState(99))
    }

    @Test
    fun `maps health states`() {
        assertEquals(BatteryHealth.GOOD, BatteryStatusMapper.mapHealth(2))
        assertEquals(BatteryHealth.OVERHEAT, BatteryStatusMapper.mapHealth(3))
        assertEquals(BatteryHealth.DEAD, BatteryStatusMapper.mapHealth(4))
        assertEquals(BatteryHealth.OVER_VOLTAGE, BatteryStatusMapper.mapHealth(5))
        assertEquals(BatteryHealth.UNSPECIFIED_FAILURE, BatteryStatusMapper.mapHealth(6))
        assertEquals(BatteryHealth.COLD, BatteryStatusMapper.mapHealth(7))
        assertEquals(BatteryHealth.UNKNOWN, BatteryStatusMapper.mapHealth(1))
    }

    @Test
    fun `maps plug source`() {
        assertEquals(PlugSource.NONE, BatteryStatusMapper.mapPlugSource(0))
        assertEquals(PlugSource.AC, BatteryStatusMapper.mapPlugSource(1))
        assertEquals(PlugSource.USB, BatteryStatusMapper.mapPlugSource(2))
        assertEquals(PlugSource.WIRELESS, BatteryStatusMapper.mapPlugSource(4))
        assertEquals(PlugSource.DOCK, BatteryStatusMapper.mapPlugSource(8))
        assertEquals(PlugSource.UNKNOWN, BatteryStatusMapper.mapPlugSource(99))
    }

    @Test
    fun `computes level percent`() {
        assertEquals(50, BatteryStatusMapper.levelPercent(level = 50, scale = 100))
        assertEquals(100, BatteryStatusMapper.levelPercent(level = 5, scale = 5))
        assertEquals(0, BatteryStatusMapper.levelPercent(level = 0, scale = 100))
        // Invalid scale must not divide by zero / crash.
        assertEquals(0, BatteryStatusMapper.levelPercent(level = 10, scale = 0))
    }

    @Test
    fun `converts temperature tenths of a degree to celsius`() {
        assertEquals(25.0f, BatteryStatusMapper.temperatureCelsius(250), 0.001f)
        assertEquals(-5.0f, BatteryStatusMapper.temperatureCelsius(-50), 0.001f)
    }

    @Test
    fun `converts millivolts to volts`() {
        assertEquals(4.2f, BatteryStatusMapper.voltageVolts(4200), 0.001f)
    }
}
