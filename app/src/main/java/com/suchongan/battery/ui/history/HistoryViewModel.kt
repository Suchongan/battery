package com.suchongan.battery.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suchongan.battery.data.battery.BatteryRepository
import com.suchongan.battery.data.db.BatterySample
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(private val repository: BatteryRepository) : ViewModel() {

    private val _selectedRange = MutableStateFlow(HistoryRange.TWENTY_FOUR_HOURS)
    val selectedRange: StateFlow<HistoryRange> = _selectedRange.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val samples: StateFlow<List<BatterySample>> = _selectedRange
        .flatMapLatest { range -> repository.historyFlow(System.currentTimeMillis() - range.durationMillis) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectRange(range: HistoryRange) {
        _selectedRange.value = range
    }
}
