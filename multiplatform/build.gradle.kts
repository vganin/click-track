@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.compose
import javax.xml.parsers.DocumentBuilderFactory

@Suppress("DSL_SCOPE_VIOLATION") // FIXME(https://github.com/gradle/gradle/issues/22797)
plugins {
    id("clicktrack.multiplatform.android")
    id("clicktrack.multiplatform.ios")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.kover)
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    kotlin("plugin.parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("androidx.compose.animation.ExperimentalAnimationApi")
                optIn("androidx.compose.animation.core.InternalAnimationApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("androidx.compose.material.ExperimentalMaterialApi")
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
                optIn("androidx.compose.ui.text.ExperimentalTextApi")
                optIn("com.russhwolf.settings.ExperimentalSettingsApi")
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.coroutines.DelicateCoroutinesApi")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.coroutines.FlowPreview")
                optIn("kotlinx.coroutines.InternalCoroutinesApi")
                optIn("kotlinx.coroutines.ObsoleteCoroutinesApi")
                optIn("kotlin.experimental.ExperimentalNativeApi")
            }
        }

        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
                api(compose.material)
                api(compose.materialIconsExtended)
                api(compose("org.jetbrains.compose.ui:ui-util")) // FIXME(https://github.com/JetBrains/compose-jb/issues/1295): Replace with `org.jetbrains.compose.ComposePlugin.Dependencies` field when it's available
                api(libs.simpleIcons)
                api(libs.bundles.decompose)
                api(libs.bundles.kotlinx.serialization)
                api(libs.kotlinx.datetime)
                api(libs.moko.resources)
                api(libs.uuid)
                api(libs.kotlininject.runtime)
                api(libs.sqldelight.runtime)
                api(libs.sqldelight.coroutines)
                api(libs.multiplatformSettings)
                api(libs.multiplatformSettings.coroutines)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        androidMain {
            dependencies {
                api(compose.uiTooling)
                api(compose.preview)
                api(libs.androidx.activity.compose)
                api(libs.androidx.constraintLayout.compose)
                api(libs.androidx.dataStore)
                api(libs.bundles.accompanist)
                api(libs.sqldelight.androidDriver)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.mockk.android)
                implementation(libs.mockk.agent)
            }
        }

        iosMain {
            dependencies {
                api(libs.sqldelight.nativeDriver)
            }
        }
    }

    cocoapods {
        name = "ClickTrackMultiplatform"
        version = "1.0"
        summary = "Shared multiplatform code"
        homepage = "https://www.vsevolodganin.com/"

        ios.deploymentTarget = "13.5"

        framework {
            baseName = "ClickTrackMultiplatform"
        }

        podfile = rootProject.file("ios-app/Podfile")
    }
}

android {
    namespace = "com.vsevolodganin.clicktrack.multiplatform"
}

multiplatformResources {
    resourcesPackage.set("com.vsevolodganin.clicktrack.generated.resources")
    resourcesClassName.set("MR")
}

sqldelight {
    databases {
        create("Database") {
            packageName = "com.vsevolodganin.clicktrack"
            schemaOutputDirectory = file("src/commonTest/sqldelight/schema")
        }
    }
}

// FIXME(https://github.com/google/ksp/issues/567): Improve KSP declarations
dependencies {
    for (configName in arrayOf(
        "kspCommonMainMetadata",
        "kspAndroid",
        "kspIosArm64",
        "kspIosSimulatorArm64"
    )) {
        add(configName, libs.kotlininject.compiler)
    }
}

tasks.register("printLineCoverage") {
    group = "verification" // Put into the same group as the `kover` tasks
    dependsOn("koverXmlReportRelease")
    doLast {
        val report = file("$buildDir/reports/kover/reportRelease.xml")

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(report)
        val rootNode = doc.firstChild
        var childNode = rootNode.firstChild

        var coveragePercent = 0.0

        while (childNode != null) {
            if (childNode.nodeName == "counter") {
                val typeAttr = childNode.attributes.getNamedItem("type")
                if (typeAttr.textContent == "LINE") {
                    val missedAttr = childNode.attributes.getNamedItem("missed")
                    val coveredAttr = childNode.attributes.getNamedItem("covered")

                    val missed = missedAttr.textContent.toLong()
                    val covered = coveredAttr.textContent.toLong()

                    coveragePercent = (covered * 100.0) / (missed + covered)

                    break
                }
            }
            childNode = childNode.nextSibling
        }

        println("%.1f".format(coveragePercent))
    }
}
