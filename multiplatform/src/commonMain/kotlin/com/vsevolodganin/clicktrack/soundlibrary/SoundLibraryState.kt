package com.vsevolodganin.clicktrack.soundlibrary

import com.vsevolodganin.clicktrack.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import kotlinx.serialization.Serializable

@Serializable
data class SoundLibraryState(
    val items: List<SelectableClickSoundsItem>,
)

@Serializable
sealed interface SelectableClickSoundsItem {
    val id: ClickSoundsId

    @Serializable
    data class Builtin(
        val data: BuiltinClickSounds,
        val selected: Boolean,
    ) : SelectableClickSoundsItem {
        override val id: ClickSoundsId get() = ClickSoundsId.Builtin(data)
    }

    @Serializable
    data class UserDefined(
        override val id: ClickSoundsId.Database,
        val strongBeatValue: String,
        val weakBeatValue: String,
        val hasError: Boolean,
        val isPlaying: Boolean,
        val selected: Boolean,
    ) : SelectableClickSoundsItem
}
