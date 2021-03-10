package com.vsevolodganin.clicktrack.sounds.model

import androidx.annotation.RawRes
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class ClickSoundSource : AndroidParcelable {

    // Shouldn't be Serializable since persisting resId for long time has no sense
    @Parcelize
    data class Bundled(@RawRes val resId: Int) : ClickSoundSource()

    @Serializable
    @Parcelize
    data class Uri(val value: String) : ClickSoundSource()
}
