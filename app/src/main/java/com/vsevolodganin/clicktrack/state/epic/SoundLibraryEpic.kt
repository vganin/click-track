package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.sounds.ClickSoundPlayer
import com.vsevolodganin.clicktrack.sounds.DocumentMetadataHelper
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.sounds.model.UserClickSounds
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.SelectableClickSoundsItem
import com.vsevolodganin.clicktrack.state.actions.SoundLibraryAction
import com.vsevolodganin.clicktrack.state.utils.onScreen
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

@ViewModelScoped
class SoundLibraryEpic @Inject constructor(
    private val store: Store<AppState>,
    private val clickSoundsRepository: ClickSoundsRepository,
    private val clickSoundPlayer: ClickSoundPlayer,
    private val userPreferences: UserPreferencesRepository,
    private val documentMetadataHelper: DocumentMetadataHelper,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            store.onScreen<Screen.SoundLibrary> {
                items().map(SoundLibraryAction::UpdateClickSoundsList)
            },

            actions.filterIsInstance<SoundLibraryAction.NewClickSounds>()
                .consumeEach {
                    clickSoundsRepository.insert(defaultNewClickSounds())
                },

            actions.filterIsInstance<SoundLibraryAction.RemoveClickSounds>()
                .consumeEach { action ->
                    clickSoundsRepository.remove(action.id)
                },

            actions.filterIsInstance<SoundLibraryAction.UpdateClickSounds>()
                .consumeEach { action ->
                    if (action.shouldStore) {
                        clickSoundsRepository.update(action.value)
                    }
                },

            actions.filterIsInstance<SoundLibraryAction.UpdateClickSound>()
                .consumeEach { action ->
                    if (action.shouldStore) {
                        clickSoundsRepository.update(action.id, action.type, action.source)
                    }
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

    private fun items(): Flow<List<SelectableClickSoundsItem>> {
        return combine(
            userPreferences.selectedSoundsIdFlow,
            clickSoundsRepository.getAll()
        ) { selectedId, userItems ->
            mutableListOf<SelectableClickSoundsItem>().apply {
                this += BuiltinClickSounds.values().map { it.toItem(selectedId) }
                this += userItems.map { it.toItem(selectedId) }
            }
        }
    }

    private fun BuiltinClickSounds.toItem(selectedId: ClickSoundsId): SelectableClickSoundsItem.Builtin {
        return SelectableClickSoundsItem.Builtin(
            data = this,
            selected = ClickSoundsId.Builtin(this) == selectedId,
        )
    }

    private fun UserClickSounds.toItem(selectedId: ClickSoundsId): SelectableClickSoundsItem.UserDefined {
        return SelectableClickSoundsItem.UserDefined(
            id = id,
            strongBeatValue = value.strongBeat.toText(),
            weakBeatValue = value.weakBeat.toText(),
            selected = selectedId == id,
        )
    }

    private fun ClickSoundSource?.toText(): String {
        return when (this) {
            is ClickSoundSource.Bundled -> "📦"
            is ClickSoundSource.Uri -> documentMetadataHelper.getDisplayName(value) ?: value
            null -> ""
        }
    }

    private fun defaultNewClickSounds() = ClickSounds(null, null)

    private fun fallbackClickSoundsId() = ClickSoundsId.Builtin(BuiltinClickSounds.BEEP)
}
