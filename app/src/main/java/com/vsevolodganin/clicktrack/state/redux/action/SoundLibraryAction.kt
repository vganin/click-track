package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.sounds.model.UserClickSounds
import com.vsevolodganin.clicktrack.state.redux.core.Action

sealed interface SoundLibraryAction : Action {

    class UpdateClickSounds(
        val value: UserClickSounds,
    ) : SoundLibraryAction

    class UpdateClickSound(
        val id: ClickSoundsId.Database,
        val type: ClickSoundType,
        val source: ClickSoundSource,
    ) : SoundLibraryAction

    object AddNewClickSounds : SoundLibraryAction

    class RemoveClickSounds(
        val id: ClickSoundsId.Database,
    ) : SoundLibraryAction

    class SelectClickSound(
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
