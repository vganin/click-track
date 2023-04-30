@file:Suppress("UnstableApiUsage")

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // FIXME(https://github.com/gradle/gradle/issues/22797)
plugins {
    id("clicktrack.android.application")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
    kotlin("android")
    kotlin("plugin.serialization")
    kotlin("plugin.parcelize")
    id("de.timfreiheit.resourceplaceholders")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val keystorePropertiesFile = file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    namespace = "com.vsevolodganin.clicktrack"

    defaultConfig {
        applicationId = "com.vsevolodganin.clicktrack"
        versionCode = 50
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

            configure<CrashlyticsExtension> {
                nativeSymbolUploadEnabled = true
            }
        }

        debug {
            applicationIdSuffix = ".debug"

            configure<CrashlyticsExtension> {
                nativeSymbolUploadEnabled = true
            }
        }

        create("perftest") {
            initWith(getByName("release"))
            applicationIdSuffix = ".perftest"
            signingConfig = signingConfigs.getByName("debug")
        }
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
    implementation(project(":multiplatform"))

    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.bundles.kotlinx.serialization)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.splashScreen)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.dataStore)
    implementation(libs.androidx.media)
    implementation(libs.androidx.workManager)
    libs.kotlininject.apply {
        ksp(compiler)
        implementation(runtime)
    }
    implementation(libs.oboe)
    implementation(libs.bundles.decompose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.googlePlay.core)
    implementation(libs.bundles.sqldelight)
    implementation(libs.timber)
}
