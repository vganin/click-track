package com.vsevolodganin.clicktrack.redux.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithSpecificId
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.MetronomeDuration
import com.vsevolodganin.clicktrack.model.MetronomeTimeSignature
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.redux.action.SoundLibraryAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@ViewModelScoped
class SoundLibraryEpic @Inject constructor(
    private val clickSoundsRepository: ClickSoundsRepository,
    private val playerServiceAccess: PlayerServiceAccess,
    private val userPreferences: UserPreferencesRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<SoundLibraryAction.AddNewClickSounds>()
                .consumeEach {
                    clickSoundsRepository.insert(defaultNewClickSounds())
                },

            actions.filterIsInstance<SoundLibraryAction.RemoveClickSounds>()
                .consumeEach { action ->
                    clickSoundsRepository.remove(action.id)
                },

            actions.filterIsInstance<SoundLibraryAction.UpdateClickSounds>()
                .consumeEach { action ->
                    clickSoundsRepository.update(action.value)
                },

            actions.filterIsInstance<SoundLibraryAction.UpdateClickSound>()
                .consumeEach { action ->
                    clickSoundsRepository.update(action.id, action.type, action.source)
                },

            actions.filterIsInstance<SoundLibraryAction.SelectClickSounds>()
                .consumeEach { action ->
                    userPreferences.selectedSoundsId.edit {
                        action.id
                    }
                },

            actions.filterIsInstance<SoundLibraryAction.RemoveClickSounds>()
                .transform { action ->
                    if (action.id == userPreferences.selectedSoundsId.stateFlow.first()) {
                        emit(SoundLibraryAction.SelectClickSounds(fallbackClickSoundsId()))
                    }
                },

            actions.filterIsInstance<SoundLibraryAction.StartSoundsTest>()
                .consumeEach { action ->
                    playerServiceAccess.start(
                        clickTrack = soundTestClickTrack(action.id),
                        atProgress = null,
                        soundsId = action.id,
                        keepInBackground = false,
                    )
                },

            actions.filterIsInstance<SoundLibraryAction.StopSoundsTest>()
                .consumeEach {
                    val currentlyPlayingId = playerServiceAccess.playbackState().firstOrNull()?.id
                    if (currentlyPlayingId is ClickTrackId.Builtin.ClickSoundsTest) {
                        playerServiceAccess.stop()
                    }
                }
        )
    }

    private fun defaultNewClickSounds() = ClickSounds(null, null)

    private fun fallbackClickSoundsId() = ClickSoundsId.Builtin(BuiltinClickSounds.BEEP)

    private fun soundTestClickTrack(soundsId: ClickSoundsId) = ClickTrackWithSpecificId(
        id = ClickTrackId.Builtin.ClickSoundsTest(soundsId),
        value = ClickTrack(
            name = "",
            cues = listOf(
                Cue(
                bpm = 120.bpm,
                pattern = NotePattern.STRAIGHT_X1,
                timeSignature = MetronomeTimeSignature,
                duration = MetronomeDuration,
            )
            ),
            loop = true,
        )
    )
}
