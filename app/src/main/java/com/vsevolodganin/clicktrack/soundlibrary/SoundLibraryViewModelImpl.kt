package com.vsevolodganin.clicktrack.soundlibrary

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.lifecycle.doOnPause
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.common.DocumentMetadataHelper
import com.vsevolodganin.clicktrack.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.UserClickSounds
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.optionalCast
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SoundLibraryViewModelImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
    private val userPreferences: UserPreferencesRepository,
    private val clickSoundsRepository: ClickSoundsRepository,
    private val documentMetadataHelper: DocumentMetadataHelper,
    private val playerServiceAccess: PlayerServiceAccess,
    private val soundChooser: SoundChooser,
) : SoundLibraryViewModel, ComponentContext by componentContext {

    private val scope = coroutineScope()

    override val state: StateFlow<SoundLibraryState?> = combine(
        userPreferences.selectedSoundsId.flow,
        clickSoundsRepository.getAll(),
        playerServiceAccess.playbackState().map { it?.id }.distinctUntilChanged(),
        ::combineToState
    ).stateIn(scope, SharingStarted.Eagerly, null)

    private fun combineToState(
        selectedId: ClickSoundsId,
        userItems: List<UserClickSounds>,
        playingId: PlayableId?,
    ): SoundLibraryState {
        return SoundLibraryState(buildList {
            this += BuiltinClickSounds.values().map { it.toItem(selectedId) }
            this += userItems.map { it.toItem(selectedId, playingId) }
        })
    }

    private fun BuiltinClickSounds.toItem(selectedId: ClickSoundsId): SelectableClickSoundsItem.Builtin {
        return SelectableClickSoundsItem.Builtin(
            data = this,
            selected = ClickSoundsId.Builtin(this) == selectedId,
        )
    }

    private fun UserClickSounds.toItem(selectedId: ClickSoundsId, playingId: PlayableId?): SelectableClickSoundsItem.UserDefined {
        return SelectableClickSoundsItem.UserDefined(
            id = id,
            strongBeatValue = value.strongBeat.toText(),
            weakBeatValue = value.weakBeat.toText(),
            hasError = value.strongBeat.hasError() || value.weakBeat.hasError(),
            isPlaying = playingId?.optionalCast<ClickTrackId.Builtin.ClickSoundsTest>()?.soundsId == id,
            selected = selectedId == id,
        )
    }

    init {
        lifecycle.doOnPause {
            val state = state.value ?: return@doOnPause
            if (state.items.any { it.optionalCast<SelectableClickSoundsItem.UserDefined>()?.isPlaying == true }) {
                playerServiceAccess.stop()
            }
        }
    }

    override fun onBackClick() = navigation.pop()

    override fun onAddNewClick() {
        scope.launch {
            clickSoundsRepository.insert(ClickSounds(null, null))
        }
    }

    override fun onItemClick(id: ClickSoundsId) {
        userPreferences.selectedSoundsId.value = id
    }

    override fun onItemRemove(id: ClickSoundsId.Database) {
        scope.launch {
            userPreferences.selectedSoundsId.edit {
                if (it == id) {
                    ClickSoundsId.Builtin(BuiltinClickSounds.BEEP)
                } else {
                    it
                }
            }
            clickSoundsRepository.remove(id)
        }
    }

    override fun onItemSoundSelect(id: ClickSoundsId.Database, type: ClickSoundType) {
        scope.launch {
            soundChooser.launchFor(id, type)
        }
    }

    override fun onItemSoundTestToggle(id: ClickSoundsId.Database) {
        val testedItem = state.value?.items?.firstOrNull { it.id == id }
            ?.optionalCast<SelectableClickSoundsItem.UserDefined>()
            ?: return

        if (testedItem.isPlaying) {
            playerServiceAccess.stop()
        } else {
            playerServiceAccess.start(ClickTrackId.Builtin.ClickSoundsTest(id), soundsId = id)
        }
    }

    private fun ClickSoundSource?.toText(): String {
        return when (this) {
            is ClickSoundSource.Bundled -> "📦"
            is ClickSoundSource.Uri -> documentMetadataHelper.getDisplayName(value) ?: value
            null -> ""
        }
    }

    private fun ClickSoundSource?.hasError(): Boolean {
        return when (this) {
            is ClickSoundSource.Bundled -> false
            is ClickSoundSource.Uri -> !documentMetadataHelper.hasReadPermission(value) || !documentMetadataHelper.isAccessible(value)
            null -> false
        }
    }
}