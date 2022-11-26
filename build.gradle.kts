import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.gradlePlugins.kotlin)
        classpath(libs.gradlePlugins.kotlin.serialization)
        classpath(libs.gradlePlugins.android)
        classpath(libs.gradlePlugins.sqlDelight)
        classpath(libs.gradlePlugins.gms)
        classpath(libs.gradlePlugins.crashlytics)
        classpath(libs.gradlePlugins.versionsPlugin)
        classpath(libs.gradlePlugins.resourcePlaceholders)
    }
}

apply(plugin = "com.github.ben-manes.versions")
apply(from = "properties.gradle.kts")

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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
        val regex = "^[0-9,.v-]+$".toRegex()
        return !regex.matches(version)
    }

    rejectVersionIf {
        isNotStable(candidate.version)
    }
}
