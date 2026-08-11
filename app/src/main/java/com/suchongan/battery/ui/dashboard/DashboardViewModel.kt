package com.suchongan.battery.ui.dashboard

import androidx.lifecycle.ViewModel
import com.suchongan.battery.data.battery.BatteryRepository
import com.suchongan.battery.data.battery.BatterySnapshot
import kotlinx.coroutines.flow.StateFlow

class DashboardViewModel(repository: BatteryRepository) : ViewModel() {
    val snapshot: StateFlow<BatterySnapshot> = repository.snapshot
}
