import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import javax.xml.parsers.DocumentBuilderFactory

plugins {
    id("clicktrack.ktlint")
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.properties)
    alias(libs.plugins.versions)
    alias(libs.plugins.kotlinx.kover)
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.kotlinx.serialization.gradlePlugin)
        classpath(libs.android.gradlePlugin)
        classpath(libs.gms.gradlePlugin)
        classpath(libs.firebase.crashlytics.gradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            val allKotlinWarningsAsErrors: String? by project
            allWarningsAsErrors.set(allKotlinWarningsAsErrors?.toBoolean() ?: true)
            freeCompilerArgs.add("-Xexpect-actual-classes")

            // FIXME: Need this because metro generates code without opt-in
            optIn.add("com.russhwolf.settings.ExperimentalSettingsApi")
        }
    }
}

tasks.withType<DependencyUpdatesTask>().configureEach {
    fun isNotStable(version: String): Boolean {
        val regex = "^[0-9,.v-]+$".toRegex()
        return !regex.matches(version)
    }

    rejectVersionIf {
        isNotStable(candidate.version)
    }
}

tasks.register("printLineCoverage") {
    group = "verification"
    dependsOn("koverXmlReport")
    doLast {
        val report = file("$buildDir/reports/kover/report.xml")

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

tasks.register("checkAndroid") {
    group = "verification"
    description = "Runs all checks on Android code"
    dependsOn(
        ":android-app:check",
        ":multiplatform:testDebugUnitTest",
        project(":multiplatform").tasks.matching {
            it.name.startsWith("ktlintCommon") || it.name.startsWith("ktlintAndroid")
        },
        ":multiplatform:verifyCommonMainDatabaseMigration",
    )
}

tasks.register("checkIos") {
    group = "verification"
    description = "Runs all checks on iOS code"
    dependsOn(
        ":multiplatform:iosSimulatorArm64Test",
        project(":multiplatform").tasks.matching {
            it.name.startsWith("ktlintCommon") || it.name.startsWith("ktlintIos")
        },
        ":multiplatform:verifyCommonMainDatabaseMigration",
    )
}
