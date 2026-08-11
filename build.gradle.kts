// Intentionally empty: plugins are declared per-module (app/build.gradle.kts,
// core/build.gradle.kts) rather than aggregated here with `apply false`. Root-level
// `apply false` plugin blocks are evaluated for every task run against any module
// (root project config always runs), which would force resolution of the Android
// Gradle Plugin even when only running `:core:test`. Declaring plugins directly in
// each module lets `:core` (a pure-Kotlin module with no Android dependency) build
// and test without ever touching Google's Maven repo.
