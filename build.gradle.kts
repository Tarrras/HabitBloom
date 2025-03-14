plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin) apply false
    //alias(libs.plugins.nativeCocoapod) apply (false)
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.spotless)
    id("dev.iurysouza.modulegraph") version "0.8.1"
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}