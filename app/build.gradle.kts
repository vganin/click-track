@file:Suppress("UnstableApiUsage")

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
    id("com.squareup.sqldelight")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("de.timfreiheit.resourceplaceholders")
}

apply(from = "generate_icons.gradle.kts")

val keystorePropertiesFile = file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    compileSdk = 33
    namespace = "com.vsevolodganin.clicktrack"

    defaultConfig {
        applicationId = "com.vsevolodganin.clicktrack"
        minSdk = 21
        targetSdk = 33
        versionCode = 46
        versionName = "1.1.0"

        resourceConfigurations += setOf("en", "ru")

        setProperty("archivesBaseName", "click-track-$versionName")

        buildConfigField("String", "DISPLAY_VERSION", "\"$versionName\"")

        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
            }
        }
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                storeFile = keystoreProperties["releaseStoreFile"]?.let(::file)
                storePassword = keystoreProperties["releaseStorePassword"] as String?
                keyAlias = keystoreProperties["releaseKeyAlias"] as String?
                keyPassword = keystoreProperties["releaseKeyPassword"] as String?
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }

            manifestPlaceholders += "crashlyticsCollectionEnabled" to "true"

            configure<CrashlyticsExtension> {
                nativeSymbolUploadEnabled = true
            }
        }

        debug {
            applicationIdSuffix = ".debug"

            manifestPlaceholders += "crashlyticsCollectionEnabled" to "false"
        }

        create("perftest") {
            initWith(getByName("release"))
            applicationIdSuffix = ".perftest"
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    sourceSets["main"].java {
        srcDir("build/generated/icons_gen")
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"

        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.ExperimentalStdlibApi",
            "-opt-in=kotlin.time.ExperimentalTime",
            "-opt-in=kotlin.contracts.ExperimentalContracts",
            "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
            "-opt-in=kotlinx.coroutines.ObsoleteCoroutinesApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.animation.core.InternalAnimationApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",

            // https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md
            "-P", "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$buildDir/reports/compose_metrics",
            "-P", "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$buildDir/reports/compose_metrics"
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }

    buildFeatures {
        prefab = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    lint {
        disable += listOf(
            // FIXME(https://issuetracker.google.com/issues/184830262)
            "NullSafeMutableLiveData",

            // FIXME(https://issuetracker.google.com/issues/189967522)
            "DialogFragmentCallbacksDetector",

            // FIXME(https://github.com/JakeWharton/timber/issues/408)
            "LogNotTimber",
            "StringFormatInTimber",
            "ThrowableNotAtBeginning",
            "BinaryOperationInTimber",
            "TimberArgCount",
            "TimberArgTypes",
            "TimberTagLength",
            "TimberExceptionLogging",

            // Not used on purpose
            "UnusedMaterialScaffoldPaddingParameter",
        )
    }
}

sqldelight {
    database("Database") {
        schemaOutputDirectory = file("src/test/sqldelight/schema")
    }
}

resourcePlaceholders {
    files = listOf("xml/shortcuts.xml")
}

dependencies {
    implementation(libs.bundles.kotlin.coroutines)
    implementation(libs.bundles.kotlin.serialization)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.splashScreen)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.dataStore)
    implementation(libs.androidx.media)
    implementation(libs.androidx.workManager)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.bundles.accompanist)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.oboe)
    implementation(libs.bundles.decompose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.googlePlay.core)
    implementation(libs.simpleIcons)
    implementation(libs.bundles.sqlDelight)
    implementation(libs.timber)
}

tasks["preBuild"].dependsOn("generateIcons")
