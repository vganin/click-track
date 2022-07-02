import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.kotlin.serialization.gradlePlugin)
        classpath(libs.android.gradlePlugin)
        classpath(libs.sqlDelight.gradlePlugin)
        classpath(libs.gms.gradlePlugin)
        classpath(libs.firebase.crashlytics.gradlePlugin)
        classpath(libs.gradleVersionsPlugin)
        classpath(libs.resourceplaceholders)
    }
}

apply(plugin = "com.github.ben-manes.versions")
apply(from = "properties.gradle.kts")

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
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            val mergedProperties: Map<String, String> by rootProject.extra
            allWarningsAsErrors = mergedProperties["allKotlinWarningsAsErrors"]?.toBoolean() ?: true
        }
    }
}

tasks.withType<DependencyUpdatesTask> {
    fun isNotStable(version: String): Boolean {
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        return regex.matches(version)
    }

    rejectVersionIf {
        isNotStable(candidate.version)
    }
}
