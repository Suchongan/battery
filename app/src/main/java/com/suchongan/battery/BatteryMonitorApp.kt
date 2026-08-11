package com.suchongan.battery

import android.app.Application
import com.suchongan.battery.data.battery.BatterySampleWorker
import com.suchongan.battery.di.AppContainer

class BatteryMonitorApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.batteryRepository.startCollecting(this)
        BatterySampleWorker.enqueuePeriodic(this)
    }
}
