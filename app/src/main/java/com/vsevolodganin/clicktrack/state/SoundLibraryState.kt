package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import kotlinx.parcelize.Parcelize

@Parcelize
data class SoundLibraryState(
    val items: List<SelectableClickSoundsItem>,
) : Parcelable

sealed class SelectableClickSoundsItem : Parcelable {

    abstract val id: ClickSoundsId

    @Parcelize
    data class Builtin(
        val data: BuiltinClickSounds,
        val selected: Boolean,
    ) : SelectableClickSoundsItem() {
        override val id: ClickSoundsId get() = ClickSoundsId.Builtin(data)
    }

    @Parcelize
    data class UserDefined(
        override val id: ClickSoundsId.Database,
        val strongBeatValue: String,
        val weakBeatValue: String,
        val selected: Boolean,
    ) : SelectableClickSoundsItem()
}
