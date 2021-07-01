package com.vsevolodganin.clicktrack.state.redux.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.sounds.ClickSoundPlayer
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.state.redux.action.SoundLibraryAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

@ViewModelScoped
class SoundLibraryEpic @Inject constructor(
    private val clickSoundsRepository: ClickSoundsRepository,
    private val clickSoundPlayer: ClickSoundPlayer,
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
                    userPreferences.selectedSoundsId = action.id
                },

            actions.filterIsInstance<SoundLibraryAction.RemoveClickSounds>()
                .transform { action ->
                    if (action.id == userPreferences.selectedSoundsId) {
                        emit(SoundLibraryAction.SelectClickSounds(fallbackClickSoundsId()))
                    }
                },

            actions.filterIsInstance<SoundLibraryAction.PlaySound>()
                .consumeEach { action ->
                    val sounds = when (val id = action.id) {
                        is ClickSoundsId.Builtin -> id.value.sounds
                        is ClickSoundsId.Database -> clickSoundsRepository.getById(id).firstOrNull()?.value
                    } ?: return@consumeEach
                    val source = sounds.beatByType(action.type)

                    if (source != null) {
                        clickSoundPlayer.play(source)
                    }
                }
        )
    }

    private fun defaultNewClickSounds() = ClickSounds(null, null)

    private fun fallbackClickSoundsId() = ClickSoundsId.Builtin(BuiltinClickSounds.BEEP)
}
