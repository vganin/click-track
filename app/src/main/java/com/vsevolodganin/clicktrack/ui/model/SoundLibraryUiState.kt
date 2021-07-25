package com.vsevolodganin.clicktrack.ui.model

import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId

data class SoundLibraryUiState(
    val items: List<SelectableClickSoundsItem>,
)

sealed interface SelectableClickSoundsItem {

    val id: ClickSoundsId

    data class Builtin(
        val data: BuiltinClickSounds,
        val selected: Boolean,
    ) : SelectableClickSoundsItem {
        override val id: ClickSoundsId get() = ClickSoundsId.Builtin(data)
    }

    data class UserDefined(
        override val id: ClickSoundsId.Database,
        val strongBeatValue: String,
        val weakBeatValue: String,
        val hasError: Boolean,
        val isPlaying: Boolean,
        val selected: Boolean,
    ) : SelectableClickSoundsItem
}
