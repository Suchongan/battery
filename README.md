# 電池監控 (Battery Monitor)

An Android battery monitoring/management app built with Kotlin + Jetpack Compose (Material 3).

## Features

- **總覽 (Dashboard)** — live battery level, charging state, health, temperature, voltage, technology, and power source.
- **歷史紀錄 (History)** — a local history of battery level over time (1h / 24h / 7d), rendered with a lightweight custom Canvas chart (no third-party charting library).
- **設定 (Settings)** — configurable low-battery alert threshold, full-charge notification toggle, and a master notifications switch with the Android 13+ runtime permission flow.

## Architecture

- **MVVM**: `ViewModel` + repository, `StateFlow`/`Flow` → Compose UI. No Hilt/Dagger — a small manual `AppContainer` (`app/.../di/AppContainer.kt`) wires everything up.
- **`:core`** — a pure-Kotlin module (no Android dependency) holding the two pieces of logic most worth unit-testing in isolation: `BatteryStatusMapper` (parses `BatteryManager` intent extras into domain types) and `AlertEvaluator` (threshold/debounce decisions for notifications). Run with `./gradlew :core:test`.
- **`:app`** — the Android application module: Compose UI, Room (history persistence), DataStore (settings), WorkManager, and notifications.
- **Data collection**: `BatteryBroadcastReceiver` is registered at runtime in `BatteryMonitorApp.onCreate()` for `ACTION_BATTERY_CHANGED` / `ACTION_POWER_CONNECTED` / `ACTION_POWER_DISCONNECTED` (this broadcast has never supported manifest/implicit registration). A `WorkManager` periodic worker (`BatterySampleWorker`, ~15 min interval — WorkManager's minimum) acts as a background safety net so history keeps accumulating even when the app process isn't alive, without requiring a persistent foreground-service notification. Note: aggressive OEM battery managers (MIUI, EMUI, etc.) can delay or skip individual worker runs.
- Room inserts are throttled (only on level change or ≥5 minutes elapsed) and pruned after 30 days. Alert-fired flags are debounced via DataStore so notifications don't repeat on every broadcast.

## Requirements

- Android Studio (Ladybug/Koala or newer recommended)
- JDK 17+
- minSdk 26, compileSdk/targetSdk 35

Dependency versions are pinned in `gradle/libs.versions.toml`. They were chosen for mutual compatibility as of early 2026 — re-check the [Compose BOM mapping](https://developer.android.com/jetpack/compose/bom/bom-mapping) and AGP release notes for anything newer before a real Play Store submission, since target-API policy requirements shift roughly once a year.

## Building & verifying

This project was scaffolded in a sandboxed environment with **no Android SDK** and **no network access to Google's Maven repo** (`dl.google.com`), so the Android-specific parts (Compose UI, Room, notifications, `BatteryManager`) could be written and reviewed but not compiled, run, or screenshotted there. `:core:test` *was* run successfully in that sandbox (13 tests passing) since it has no Android/AGP dependency.

To verify the rest, open the project in Android Studio and:

1. **Sync & build**: `Build > Make Project` (or `./gradlew assembleDebug` from a machine with normal internet access and the Android SDK installed).
2. **Unit tests**: `./gradlew :core:test` (logic tests) — should already pass as-is.
3. **Run on a device/emulator**: create an API 26 (minSdk) and an API 34/35 emulator, install, and check:
   - Dashboard updates live as the battery level/charging state changes.
   - History screen populates over time and the 1h/24h/7d range chips work.
   - Settings: toggling notifications on API 33+ triggers the permission dialog; adjusting the low-battery threshold and full-charge toggle persist across app restarts.
4. **Simulate battery events without hardware cycling** (via `adb`):
   ```
   adb shell dumpsys battery set level 15      # simulate low battery
   adb shell dumpsys battery set status 2      # simulate charging (BATTERY_STATUS_CHARGING)
   adb shell dumpsys battery set status 5      # simulate full (BATTERY_STATUS_FULL)
   adb shell dumpsys battery reset             # stop overriding, back to real battery state
   ```
5. Notifications should appear once per threshold-crossing (not repeatedly), per the debounce logic in `AlertEvaluator`.

## Project structure

```
core/                                  pure-Kotlin logic module (no Android dependency)
  src/main/.../core/BatteryStatusMapper.kt
  src/main/.../core/AlertEvaluator.kt
app/
  src/main/java/com/suchongan/battery/
    BatteryMonitorApp.kt               Application: registers receiver, enqueues worker
    MainActivity.kt
    data/battery/                      BatterySnapshot, receiver, repository, worker
    data/db/                           Room entity/DAO/database (history)
    data/settings/                     DataStore-backed settings
    di/AppContainer.kt                 manual DI
    notification/NotificationHelper.kt
    ui/dashboard/ ui/history/ ui/settings/ ui/navigation/ ui/theme/
```
