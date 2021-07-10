package com.vsevolodganin.clicktrack.state.presenter

import com.vsevolodganin.clicktrack.sounds.DocumentMetadataHelper
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.sounds.model.UserClickSounds
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.ui.model.SelectableClickSoundsItem
import com.vsevolodganin.clicktrack.ui.model.SoundLibraryUiState
import com.vsevolodganin.clicktrack.ui.model.UiScreen
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Reusable
class SoundLibraryPresenter @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val clickSoundsRepository: ClickSoundsRepository,
    private val documentMetadataHelper: DocumentMetadataHelper,
) {
    fun uiScreens(): Flow<UiScreen.SoundLibrary> {
        return items()
            .map(::SoundLibraryUiState)
            .map(UiScreen::SoundLibrary)
    }

    private fun items(): Flow<List<SelectableClickSoundsItem>> {
        return combine(
            userPreferencesRepository.selectedSoundsId.flow,
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
            strongBeatHasError = value.strongBeat.hasError(),
            weakBeatValue = value.weakBeat.toText(),
            weakBeatHasError = value.weakBeat.hasError(),
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

    private fun ClickSoundSource?.hasError(): Boolean {
        return when (this) {
            is ClickSoundSource.Bundled -> false
            is ClickSoundSource.Uri -> !documentMetadataHelper.hasReadPermission(value) || !documentMetadataHelper.isAccessible(value)
            null -> false
        }
    }
}
