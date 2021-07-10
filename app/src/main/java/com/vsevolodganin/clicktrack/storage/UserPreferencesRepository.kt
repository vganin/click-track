package com.vsevolodganin.clicktrack.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.theme.Theme
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@ApplicationScoped
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    object Const {
        const val NO_APP_VERSION_CODE = -1
    }

    interface UserPropertyAccess<T> {
        val flow: Flow<T>
        suspend fun edit(transform: (T) -> T)
    }

    val appVersionCode: UserPropertyAccess<Int> =
        UserPropertyAccessWithNoMapping(
            key = intPreferencesKey("app_version_code"),
            defaultValue = Const.NO_APP_VERSION_CODE,
        )

    val metronomeBpm: UserPropertyAccess<BeatsPerMinute> =
        UserPropertyAccessWithMapping(
            key = intPreferencesKey("metronome_bpm"),
            defaultValue = 120.bpm,
            toExternal = { it.bpm },
            toInternal = { it.value },
        )

    val metronomePattern: UserPropertyAccess<NotePattern> =
        UserPropertyAccessWithMapping(
            key = stringPreferencesKey("metronome_pattern"),
            defaultValue = NotePattern.STRAIGHT_X1,
            toExternal = { NotePattern.valueOf(it) },
            toInternal = { it.toString() }
        )

    val theme: UserPropertyAccess<Theme> =
        UserPropertyAccessWithMapping(
            key = stringPreferencesKey("theme"),
            defaultValue = Theme.SYSTEM,
            toExternal = { Theme.valueOf(it) },
            toInternal = { it.toString() }
        )

    val selectedSoundsId: UserPropertyAccess<ClickSoundsId> =
        UserPropertyAccessWithMapping(
            key = stringPreferencesKey("selected_sounds_id"),
            defaultValue = ClickSoundsId.Builtin(BuiltinClickSounds.BEEP),
            toExternal = { stringValue ->
                stringValue.toLongOrNull()?.let(ClickSoundsId::Database)
                    ?: BuiltinClickSounds.values().firstOrNull { it.storageKey == stringValue }?.let(ClickSoundsId::Builtin)
                    ?: ClickSoundsId.Builtin(BuiltinClickSounds.BEEP)
            },
            toInternal = {
                when (it) {
                    is ClickSoundsId.Database -> it.value.toString()
                    is ClickSoundsId.Builtin -> it.value.storageKey
                }
            },
        )

    private open inner class UserPropertyAccessWithMapping<TInternal, TExternal>(
        private val key: Preferences.Key<TInternal>,
        private val defaultValue: TExternal,
        private val toExternal: (TInternal) -> TExternal,
        private val toInternal: (TExternal) -> TInternal,
    ) : UserPropertyAccess<TExternal> {
        override val flow: Flow<TExternal> = dataStore.data
            .map { preferences ->
                preferences[key]?.let(toExternal) ?: defaultValue
            }

        override suspend fun edit(transform: (TExternal) -> TExternal) {
            dataStore.edit { preferences ->
                preferences[key] = toInternal(transform(preferences[key]?.let(toExternal) ?: defaultValue))
            }
        }
    }

    private inner class UserPropertyAccessWithNoMapping<T>(
        key: Preferences.Key<T>,
        defaultValue: T,
    ) : UserPropertyAccessWithMapping<T, T>(
        key = key,
        defaultValue = defaultValue,
        toExternal = { it },
        toInternal = { it },
    )
}

val <T> UserPreferencesRepository.UserPropertyAccess<T>.blockingValue: T
    get() {
        return runBlocking { flow.first() }
    }
