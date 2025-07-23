package com.vsevolodganin.clicktrack.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository.Const.NO_APP_VERSION_CODE
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository.Const.REVIEW_NOT_REQUESTED
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.training.TrainingEditState.TrainingMode
import com.vsevolodganin.clicktrack.training.TrainingValidState
import com.vsevolodganin.clicktrack.utils.settings.PreferenceKey
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalSettingsApi::class)
@ApplicationScope
@Inject
class UserPreferencesRepository(
    private val settings: FlowSettings,
    private val json: Json,
) {
    object Const {
        const val NO_APP_VERSION_CODE = -1
        const val REVIEW_NOT_REQUESTED = -1L
    }

    interface UserPropertyAccess<T> {
        val flow: Flow<T>
        var value: T

        fun edit(transform: (T) -> T)
    }

    val appVersionCode: UserPropertyAccess<Int> = UserPropertyAccessWithNoMapping(
        key = PreferenceKey.Int("app_version_code"),
        defaultValue = NO_APP_VERSION_CODE,
    )

    val reviewRequestTimestamp: UserPropertyAccess<Long> = UserPropertyAccessWithNoMapping(
        key = PreferenceKey.Long("review_request_timestamp"),
        defaultValue = REVIEW_NOT_REQUESTED,
    )

    val metronomeBpm: UserPropertyAccess<BeatsPerMinute> = UserPropertyAccessWithMapping(
        key = PreferenceKey.Int("metronome_bpm"),
        defaultValue = 120.bpm,
        toExternal = { it.bpm },
        toInternal = { it.value },
    )

    val metronomePattern: UserPropertyAccess<NotePattern> = UserPropertyAccessWithMapping(
        key = PreferenceKey.String("metronome_pattern"),
        defaultValue = NotePattern.STRAIGHT_X1,
        toExternal = { NotePattern.valueOf(it) },
        toInternal = { it.toString() },
    )

    val theme: UserPropertyAccess<Theme> = UserPropertyAccessWithMapping(
        key = PreferenceKey.String("theme"),
        defaultValue = Theme.SYSTEM,
        toExternal = { Theme.valueOf(it) },
        toInternal = { it.toString() },
    )

    val selectedSoundsId: UserPropertyAccess<ClickSoundsId> = UserPropertyAccessWithMapping(
        key = PreferenceKey.String("selected_sounds_id"),
        defaultValue = ClickSoundsId.Builtin(BuiltinClickSounds.BEEP),
        toExternal = { stringValue ->
            stringValue.toLongOrNull()?.let(ClickSoundsId::Database)
                ?: BuiltinClickSounds.entries.firstOrNull { it.storageKey == stringValue }?.let(ClickSoundsId::Builtin)
                ?: ClickSoundsId.Builtin(BuiltinClickSounds.BEEP)
        },
        toInternal = {
            when (it) {
                is ClickSoundsId.Database -> it.value.toString()
                is ClickSoundsId.Builtin -> it.value.storageKey
            }
        },
    )

    val trainingState: UserPropertyAccess<TrainingValidState> = UserPropertyAccessWithMapping(
        key = PreferenceKey.String("training_state"),
        defaultValue = TrainingValidState(
            startingTempo = 120.bpm,
            mode = TrainingMode.INCREASE_TEMPO,
            segmentLength = CueDuration.Measures(4),
            tempoChange = 5.bpm,
            ending = TrainingValidState.Ending.ByTempo(160.bpm),
        ),
        toExternal = json::decodeFromString,
        toInternal = json::encodeToString,
    )

    val polyrhythm: UserPropertyAccess<TwoLayerPolyrhythm> = UserPropertyAccessWithMapping(
        key = PreferenceKey.String("polyrhythm"),
        defaultValue = TwoLayerPolyrhythm(
            bpm = 120.bpm,
            layer1 = 3,
            layer2 = 2,
        ),
        toExternal = json::decodeFromString,
        toInternal = json::encodeToString,
    )

    val ignoreAudioFocus: UserPropertyAccess<Boolean> = UserPropertyAccessWithNoMapping(
        key = PreferenceKey.Boolean("ignore_audio_focus"),
        defaultValue = false,
    )

    val playTrackingMode: UserPropertyAccess<Boolean> = UserPropertyAccessWithNoMapping(
        key = PreferenceKey.Boolean("play_tracking_mode"),
        defaultValue = true,
    )

    @OptIn(ExperimentalSettingsApi::class, DelicateCoroutinesApi::class)
    private open inner class UserPropertyAccessWithMapping<TInternal, TExternal>(
        private val key: PreferenceKey<TInternal>,
        private val defaultValue: TExternal,
        private val toExternal: (TInternal) -> TExternal,
        private val toInternal: (TExternal) -> TInternal,
    ) : UserPropertyAccess<TExternal> {
        private val stateFlow = MutableStateFlow(
            runBlocking {
                with(key) { settings.get() }?.let(toExternal) ?: defaultValue
            },
        )

        init {
            GlobalScope.launch(Dispatchers.Default, CoroutineStart.UNDISPATCHED) {
                stateFlow.drop(1).collect { value ->
                    with(key) {
                        settings.put(toInternal(value))
                    }
                }
            }
        }

        override val flow: Flow<TExternal> = stateFlow

        override var value: TExternal
            get() = stateFlow.value
            set(value) {
                stateFlow.value = value
            }

        override fun edit(transform: (TExternal) -> TExternal) = stateFlow.update(transform)
    }

    private inner class UserPropertyAccessWithNoMapping<T>(
        key: PreferenceKey<T>,
        defaultValue: T,
    ) : UserPropertyAccessWithMapping<T, T>(
        key = key,
        defaultValue = defaultValue,
        toExternal = { it },
        toInternal = { it },
    )
}
