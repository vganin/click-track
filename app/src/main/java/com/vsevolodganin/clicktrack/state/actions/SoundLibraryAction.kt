package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.sounds.model.UserClickSounds
import com.vsevolodganin.clicktrack.state.screen.SelectableClickSoundsItem

sealed interface SoundLibraryAction : Action {

    object SubscribeToData : SoundLibraryAction {
        object Dispose : SoundLibraryAction
    }

    class UpdateClickSoundsList(
        val items: List<SelectableClickSoundsItem>,
    ) : SoundLibraryAction

    class UpdateClickSounds(
        val value: UserClickSounds,
        val shouldStore: Boolean,
    ) : SoundLibraryAction

    class UpdateClickSound(
        val id: ClickSoundsId.Database,
        val type: ClickSoundType,
        val source: ClickSoundSource,
        val shouldStore: Boolean,
    ) : SoundLibraryAction

    object NewClickSounds : SoundLibraryAction

    class RemoveClickSounds(
        val id: ClickSoundsId.Database,
    ) : SoundLibraryAction

    class ChooseClickSound(
        val id: ClickSoundsId.Database,
        val type: ClickSoundType,
    ) : SoundLibraryAction

    class SelectClickSounds(
        val id: ClickSoundsId,
    ) : SoundLibraryAction

    class PlaySound(
        val id: ClickSoundsId,
        val type: ClickSoundType,
    ) : SoundLibraryAction
}
