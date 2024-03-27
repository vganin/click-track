import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

@Suppress("DSL_SCOPE_VIOLATION") // FIXME(https://github.com/gradle/gradle/issues/22797)
plugins {
    alias(libs.plugins.jetbrains.compose)
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
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.kotlinx.serialization.gradlePlugin)
        classpath(libs.android.gradlePlugin)
        classpath(libs.gms.gradlePlugin)
        classpath(libs.firebase.crashlytics.gradlePlugin)
        classpath(libs.moko.resources.gradlePlugin)
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
            freeCompilerArgs.set(listOf("-Xexpect-actual-classes"))
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
