package com.suchongan.battery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.suchongan.battery.ui.LocalAppContainer
import com.suchongan.battery.ui.navigation.BatteryNavHost
import com.suchongan.battery.ui.theme.BatteryTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as BatteryMonitorApp).container

        setContent {
            CompositionLocalProvider(LocalAppContainer provides container) {
                BatteryTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        BatteryNavHost()
                    }
                }
            }
        }
    }
}
