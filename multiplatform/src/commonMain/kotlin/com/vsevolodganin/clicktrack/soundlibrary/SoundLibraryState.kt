package com.vsevolodganin.clicktrack.soundlibrary

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.vsevolodganin.clicktrack.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundsId

@Parcelize
data class SoundLibraryState(
    val items: List<SelectableClickSoundsItem>,
) : Parcelable

@Parcelize
sealed interface SelectableClickSoundsItem : Parcelable {

    val id: ClickSoundsId

    @Parcelize
    data class Builtin(
        val data: BuiltinClickSounds,
        val selected: Boolean,
    ) : SelectableClickSoundsItem {
        override val id: ClickSoundsId get() = ClickSoundsId.Builtin(data)
    }

    @Parcelize
    data class UserDefined(
        override val id: ClickSoundsId.Database,
        val strongBeatValue: String,
        val weakBeatValue: String,
        val hasError: Boolean,
        val isPlaying: Boolean,
        val selected: Boolean,
    ) : SelectableClickSoundsItem
}
