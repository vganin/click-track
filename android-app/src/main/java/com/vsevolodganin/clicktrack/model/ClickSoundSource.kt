package com.vsevolodganin.clicktrack.model

import android.os.Parcelable
import androidx.annotation.RawRes
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed class ClickSoundSource : Parcelable {

    @Parcelize
    data class Bundled(@RawRes val resId: Int) : ClickSoundSource()

    @Serializable
    @Parcelize
    data class Uri(val value: String) : ClickSoundSource()
}
