package com.suchongan.battery.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.suchongan.battery.di.AppContainer

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("LocalAppContainer not provided — wrap content in CompositionLocalProvider from MainActivity")
}
