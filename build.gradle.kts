import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.properties)
    alias(libs.plugins.versions)
}

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
        classpath(libs.gradlePlugins.resourcePlaceholders)
        classpath(libs.gradlePlugins.mokoResources)
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
