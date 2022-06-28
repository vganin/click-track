package com.vsevolodganin.clicktrack.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.redux.TrainingMode
import com.vsevolodganin.clicktrack.redux.TrainingPersistableState
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository.Const.NO_APP_VERSION_CODE
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository.Const.REVIEW_NOT_REQUESTED
import com.vsevolodganin.clicktrack.theme.Theme
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) {
    object Const {
        const val NO_APP_VERSION_CODE = -1
        const val REVIEW_NOT_REQUESTED = -1L
    }

    interface UserPropertyAccess<T> {
        val stateFlow: StateFlow<T>
        suspend fun edit(transform: (T) -> T)
    }

    val appVersionCode: UserPropertyAccess<Int> = UserPropertyAccessWithNoMapping(
        key = intPreferencesKey("app_version_code"),
        defaultValue = NO_APP_VERSION_CODE,
    )

    val reviewRequestTimestamp: UserPropertyAccess<Long> = UserPropertyAccessWithNoMapping(
        key = longPreferencesKey("review_request_timestamp"),
        defaultValue = REVIEW_NOT_REQUESTED
    )

    val metronomeBpm: UserPropertyAccess<BeatsPerMinute> = UserPropertyAccessWithMapping(
        key = intPreferencesKey("metronome_bpm"),
        defaultValue = 120.bpm,
        toExternal = { it.bpm },
        toInternal = { it.value },
    )

    val metronomePattern: UserPropertyAccess<NotePattern> = UserPropertyAccessWithMapping(
        key = stringPreferencesKey("metronome_pattern"),
        defaultValue = NotePattern.STRAIGHT_X1,
        toExternal = { NotePattern.valueOf(it) },
        toInternal = { it.toString() }
    )

    val theme: UserPropertyAccess<Theme> = UserPropertyAccessWithMapping(
        key = stringPreferencesKey("theme"),
        defaultValue = Theme.SYSTEM,
        toExternal = { Theme.valueOf(it) },
        toInternal = { it.toString() }
    )

    val selectedSoundsId: UserPropertyAccess<ClickSoundsId> = UserPropertyAccessWithMapping(
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

    val trainingState: UserPropertyAccess<TrainingPersistableState> = UserPropertyAccessWithMapping(
        key = stringPreferencesKey("training_state"),
        defaultValue = TrainingPersistableState(
            startingTempo = 120.bpm,
            mode = TrainingMode.INCREASE_TEMPO,
            segmentLength = CueDuration.Measures(4),
            tempoChange = 5.bpm,
            ending = TrainingPersistableState.Ending.ByTempo(160.bpm),
        ),
        toExternal = json::decodeFromString,
        toInternal = json::encodeToString,
    )

    val polyrhythm: UserPropertyAccess<TwoLayerPolyrhythm> = UserPropertyAccessWithMapping(
        key = stringPreferencesKey("polyrhythm"),
        defaultValue = TwoLayerPolyrhythm(
            bpm = 120.bpm,
            layer1 = 3,
            layer2 = 2,
        ),
        toExternal = json::decodeFromString,
        toInternal = json::encodeToString,
    )

    val ignoreAudioFocus: UserPropertyAccess<Boolean> = UserPropertyAccessWithNoMapping(
        key = booleanPreferencesKey("ignore_audio_focus"),
        defaultValue = false,
    )

    private open inner class UserPropertyAccessWithMapping<TInternal, TExternal>(
        private val key: Preferences.Key<TInternal>,
        private val defaultValue: TExternal,
        private val toExternal: (TInternal) -> TExternal,
        private val toInternal: (TExternal) -> TInternal,
    ) : UserPropertyAccess<TExternal> {

        private val _stateFlow = MutableStateFlow(runBlocking {
            dataStore.data
                .map { preferences ->
                    preferences[key]?.let(toExternal) ?: defaultValue
                }
                .first()
        })

        init {
            GlobalScope.launch(Dispatchers.Default, CoroutineStart.UNDISPATCHED) {
                _stateFlow.drop(1).collect {
                    dataStore.edit { preferences ->
                        preferences[key] = toInternal(it)
                    }
                }
            }
        }

        override val stateFlow: StateFlow<TExternal> = _stateFlow

        override suspend fun edit(transform: (TExternal) -> TExternal) {
            do {
                val currentValue = stateFlow.value
                val newValue = transform(currentValue ?: defaultValue)
            } while (!_stateFlow.compareAndSet(currentValue, newValue))
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
