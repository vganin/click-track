package com.vsevolodganin.clicktrack.migration

import com.vsevolodganin.clicktrack.common.BuildConfig
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import me.tatarka.inject.annotations.Inject

@MainControllerScope
@Inject
class MigrationManager(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val canMigrate: Set<CanMigrate>,
    private val buildConfig: BuildConfig,
) {
    fun tryMigrate() {
        userPreferencesRepository.appVersionCode.edit { fromVersion ->
            val toVersion = buildConfig.versionCode

            if (fromVersion == toVersion) return@edit toVersion

            canMigrate.forEach { it.migrate(fromVersion, toVersion) }

            toVersion
        }
    }
}
