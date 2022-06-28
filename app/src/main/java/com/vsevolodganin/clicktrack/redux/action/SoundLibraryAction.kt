package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.sounds.model.UserClickSounds

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

    class StartSoundsTest(
        val id: ClickSoundsId,
    ) : SoundLibraryAction

    object StopSoundsTest : SoundLibraryAction
}
