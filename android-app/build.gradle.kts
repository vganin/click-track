@file:Suppress("UnstableApiUsage")

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    id("clicktrack.android.application")
    id("clicktrack.include-in-coverage")
    id("clicktrack.ktlint")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    kotlin("android")
    kotlin("plugin.serialization")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val keystorePropertiesFile = file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

val baseVersion = "1.3.1"
val currentDate = SimpleDateFormat("yyyyMMdd").format(Date())!!

android {
    namespace = "com.vsevolodganin.clicktrack"

    defaultConfig {
        applicationId = "com.vsevolodganin.clicktrack"
        versionCode = file("version-code").readText().trim().toInt()
        versionName = "$baseVersion ($currentDate)"

        resourceConfigurations += setOf("en", "ru")

        setProperty("archivesBaseName", "click-track-$baseVersion-$currentDate")

        externalNativeBuild {
            cmake {
                arguments += listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON",
                )
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
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(project(":multiplatform"))

    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.bundles.kotlinx.serialization)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.splashScreen)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.media)
    implementation(libs.androidx.workManager)
    implementation(libs.bundles.decompose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.googlePlay.review)
    ksp(libs.kotlininject.compiler)

    testImplementation(kotlin("test"))
    testImplementation(libs.testParameterInjector)

    coreLibraryDesugaring(libs.desugarJdkLibs)
}
