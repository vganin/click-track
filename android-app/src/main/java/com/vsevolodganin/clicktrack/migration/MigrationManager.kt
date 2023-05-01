package com.vsevolodganin.clicktrack.migration

import com.vsevolodganin.clicktrack.BuildConfig
import com.vsevolodganin.clicktrack.di.component.ActivityScope
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import me.tatarka.inject.annotations.Inject

@ActivityScope
@Inject
class MigrationManager(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val canMigrate: Set<@JvmSuppressWildcards CanMigrate>,
) {
    fun tryMigrate() {
        userPreferencesRepository.appVersionCode.edit { fromVersion ->
            val toVersion = BuildConfig.VERSION_CODE

            if (fromVersion == toVersion) return@edit toVersion

            canMigrate.forEach { it.migrate(fromVersion, toVersion) }

            toVersion
        }
    }
}
