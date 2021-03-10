package com.vsevolodganin.clicktrack.migration

import com.vsevolodganin.clicktrack.BuildConfig
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import javax.inject.Inject

@ActivityScoped
class MigrationManager @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val canMigrate: Set<@JvmSuppressWildcards CanMigrate>,
) {
    fun tryMigrate() {
        val fromVersion = userPreferencesRepository.appVersionCode
        val toVersion = BuildConfig.VERSION_CODE

        if (fromVersion == toVersion) return

        canMigrate.forEach { it.migrate(fromVersion, toVersion) }

        userPreferencesRepository.appVersionCode = toVersion
    }
}
