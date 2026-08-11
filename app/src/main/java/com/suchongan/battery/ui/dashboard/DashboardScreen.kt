package com.suchongan.battery.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suchongan.battery.R
import com.suchongan.battery.core.BatteryHealth
import com.suchongan.battery.core.ChargingState
import com.suchongan.battery.core.PlugSource
import com.suchongan.battery.data.battery.BatterySnapshot
import com.suchongan.battery.ui.LocalAppContainer
import com.suchongan.battery.ui.ViewModelFactory

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    val viewModel: DashboardViewModel = viewModel(factory = ViewModelFactory(LocalAppContainer.current))
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    DashboardContent(snapshot = snapshot, modifier = modifier)
}

@Composable
private fun DashboardContent(snapshot: BatterySnapshot, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { LevelCard(snapshot) }
        item {
            val rows = listOf(
                stringResource(R.string.dashboard_health) to healthLabel(snapshot.health),
                stringResource(R.string.dashboard_temperature) to "%.1f°C".format(snapshot.temperatureCelsius),
                stringResource(R.string.dashboard_voltage) to "%.2fV".format(snapshot.voltageVolts),
                stringResource(R.string.dashboard_technology) to snapshot.technology.ifBlank { "-" },
                stringResource(R.string.dashboard_plug_source) to plugSourceLabel(snapshot.plugSource),
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    rows.forEachIndexed { index, (label, value) ->
                        StatRow(label = label, value = value)
                        if (index != rows.lastIndex) Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelCard(snapshot: BatterySnapshot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = if (snapshot.chargingState == ChargingState.CHARGING || snapshot.chargingState == ChargingState.FULL) {
                    Icons.Filled.BatteryChargingFull
                } else {
                    Icons.Filled.BatteryStd
                },
                contentDescription = null,
                modifier = Modifier.height(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "${snapshot.levelPercent}%",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = chargingStateLabel(snapshot.chargingState),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun chargingStateLabel(state: ChargingState): String = stringResource(
    when (state) {
        ChargingState.CHARGING -> R.string.dashboard_charging
        ChargingState.DISCHARGING -> R.string.dashboard_discharging
        ChargingState.FULL -> R.string.dashboard_full
        ChargingState.NOT_CHARGING -> R.string.dashboard_not_charging
        ChargingState.UNKNOWN -> R.string.dashboard_unknown_state
    },
)

@Composable
private fun healthLabel(health: BatteryHealth): String = stringResource(
    when (health) {
        BatteryHealth.GOOD -> R.string.health_good
        BatteryHealth.OVERHEAT -> R.string.health_overheat
        BatteryHealth.DEAD -> R.string.health_dead
        BatteryHealth.OVER_VOLTAGE -> R.string.health_over_voltage
        BatteryHealth.UNSPECIFIED_FAILURE -> R.string.health_unspecified_failure
        BatteryHealth.COLD -> R.string.health_cold
        BatteryHealth.UNKNOWN -> R.string.health_unknown
    },
)

@Composable
private fun plugSourceLabel(source: PlugSource): String = stringResource(
    when (source) {
        PlugSource.AC -> R.string.plug_source_ac
        PlugSource.USB -> R.string.plug_source_usb
        PlugSource.WIRELESS -> R.string.plug_source_wireless
        PlugSource.DOCK -> R.string.plug_source_dock
        PlugSource.NONE -> R.string.plug_source_none
        PlugSource.UNKNOWN -> R.string.plug_source_unknown
    },
)
