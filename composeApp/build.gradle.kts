import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinX.serialization.plugin)
    id("app.cash.sqldelight") version "2.0.2"
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {

        androidMain.dependencies {
            implementation(libs.android.driver)

            implementation(libs.accompanist.systemUIController)
            implementation(libs.core)
            implementation(libs.compose.activity)

            implementation(libs.firebase.bom)
            implementation(libs.firebase.common.ktx)
            implementation(libs.firebase.android.crashlytics.ktx)
        }

        iosMain.dependencies {
            implementation(libs.native.driver)


            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
        }

        commonMain.dependencies {
            api(libs.koin.core)
            api(libs.koin.compose)

            implementation(compose.material3)
            implementation(compose.materialIconsExtended)

            implementation(compose.components.resources)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.bottomSheetNavigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.koin)

            implementation(libs.kotlinX.serializationJson)

            implementation(libs.material3.window.size.multiplatform)

            implementation(libs.sqlDelight.runtime)
            implementation(libs.coroutines.extensions)
            implementation(libs.primitive.adapters)

            api(libs.multiplatformSettings.noArg)
            api(libs.multiplatformSettings.coroutines)

            api(libs.napier)

            implementation(libs.kotlinX.dateTime)
            implementation(libs.koalaplot.core)

            implementation(libs.stdlib)

            api(libs.gitlive.firebase.kotlin.crashlytics)
        }
    }
}

android {
    namespace = "com.horizondev.habitbloom"
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.horizondev.habitbloom"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

