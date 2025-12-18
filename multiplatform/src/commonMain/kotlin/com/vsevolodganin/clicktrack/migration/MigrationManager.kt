package com.vsevolodganin.clicktrack.migration

import com.vsevolodganin.clicktrack.common.ApplicationBuildConfig
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(MainControllerScope::class)
@Inject
class MigrationManager(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val canMigrate: Set<CanMigrate>,
    private val applicationBuildConfig: ApplicationBuildConfig,
) {
    fun tryMigrate() {
        userPreferencesRepository.appVersionCode.edit { fromVersion ->
            val toVersion = applicationBuildConfig.versionCode

            if (fromVersion == toVersion) return@edit toVersion

            canMigrate.forEach { it.migrate(fromVersion, toVersion) }

            toVersion
        }
    }
}
