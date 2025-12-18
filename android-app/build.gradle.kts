@file:Suppress("UnstableApiUsage")

import java.util.Properties

plugins {
    id("clicktrack.android.application")
    id("clicktrack.include-in-coverage")
    id("clicktrack.ktlint")
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
        versionCode = providers.gradleProperty("clicktrack.android.versionCode").get().toInt()
        versionName = providers.gradleProperty("clicktrack.android.versionName").get()

        resourceConfigurations += setOf("en", "ru")

        setProperty("archivesBaseName", "click-track-v$versionName")
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
        }

        debug {
            applicationIdSuffix = ".debug"
        }
    }
}

dependencies {
    implementation(project(":multiplatform"))

    // Needed because androidx.appcompat.app.AppLocalesMetadataHolderService is declared in AndroidManifest.xml
    implementation(libs.androidx.appcompat)

    // Needed because androidx.work.impl.foreground.SystemForegroundService is declared in AndroidManifest.xml
    implementation(libs.androidx.workManager)

    coreLibraryDesugaring(libs.desugarJdkLibs)
}
