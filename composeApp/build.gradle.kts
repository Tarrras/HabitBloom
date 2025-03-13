import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.nativeCocoapod)
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
            jvmTarget.set(JvmTarget.JVM_17)
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

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            transitiveExport = true
            compilations.all {
                kotlinOptions.freeCompilerArgs += arrayOf(
                    "-linker-options",
                    "-lsqlite3",
                    "-Xbinary=bundleId=com.horizondev.habitbloom.HabitBloom"
                )
            }
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.android.driver)

            implementation(libs.accompanist.systemUIController)
            implementation(libs.core)
            implementation(libs.compose.activity)
            implementation(compose.preview)

            implementation(libs.koin.android)

            implementation(libs.firebase.bom)
            implementation(libs.firebase.common.ktx)
            implementation(libs.firebase.android.crashlytics.ktx)

            implementation(libs.ktor.client.okhttp)

            // Add Lottie dependency for Android
            implementation("com.airbnb.android:lottie-compose:6.1.0")
        }

        iosMain.dependencies {
            implementation(libs.native.driver)


            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            implementation(libs.stately.isolate)
            implementation(libs.stately.iso.collections)

            implementation(libs.ktor.client.darwin)
        }

        commonMain.dependencies {
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
            api(libs.koin.compose.viewmodel.navigation)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

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
            implementation(libs.gitlive.firebase.firestore)
            implementation(libs.gitlive.firebase.common)
            implementation(libs.gitlive.firebase.auth)

            //implementation(libs.supabase.bom)
            implementation(libs.supabase.storage.ktx)

            implementation(libs.stately.common)

            implementation(libs.coil.compose)
            implementation(libs.coil.gif)
            implementation(libs.coil.network.ktor3)

            implementation(libs.ktor.client.core)

            implementation(libs.calendar.compose.multiplatform)

            implementation(libs.navigation.compose)
            // KMP ViewModel
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.common)
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }

        // Update the Lottie iOS pod configuration to match our imports
        //pod("lottie-ios")
    }
}

android {
    namespace = "com.horizondev.habitbloom"
    compileSdk = 35

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.horizondev.habitbloom"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

sqldelight {
    databases {
        create("HabitBloomDatabase") {
            packageName.set("com.horizondev.habitbloom.database")
        }
    }
}

