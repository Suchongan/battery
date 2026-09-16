package com.suchongan.battery.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ChargingProtocolClassifierTest {

    @Test
    fun `not charging is none regardless of power`() {
        assertEquals(ChargingProtocol.NONE, ChargingProtocolClassifier.classify(isCharging = false, powerWatts = 20f))
    }

    @Test
    fun `charging with unavailable power is unknown`() {
        assertEquals(ChargingProtocol.UNKNOWN, ChargingProtocolClassifier.classify(isCharging = true, powerWatts = null))
    }

    @Test
    fun `low power is standard`() {
        assertEquals(ChargingProtocol.STANDARD, ChargingProtocolClassifier.classify(isCharging = true, powerWatts = 5f))
    }

    @Test
    fun `mid power is fast`() {
        assertEquals(ChargingProtocol.FAST, ChargingProtocolClassifier.classify(isCharging = true, powerWatts = 10f))
    }

    @Test
    fun `high power is super fast`() {
        assertEquals(ChargingProtocol.SUPER_FAST, ChargingProtocolClassifier.classify(isCharging = true, powerWatts = 25f))
    }

    @Test
    fun `tier boundaries round up to the next tier`() {
        assertEquals(ChargingProtocol.FAST, ChargingProtocolClassifier.classify(isCharging = true, powerWatts = 7.5f))
        assertEquals(ChargingProtocol.SUPER_FAST, ChargingProtocolClassifier.classify(isCharging = true, powerWatts = 18f))
    }
}
