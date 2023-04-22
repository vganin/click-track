package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.parcelable.TypeParceler
import com.vsevolodganin.clicktrack.utils.resources.FileResourceParceler
import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.Serializable

sealed class ClickSoundSource : Parcelable {

    @Parcelize
    @TypeParceler<FileResource, FileResourceParceler>()
    data class Bundled(val audioResource: FileResource) : ClickSoundSource()

    @Serializable
    @Parcelize
    data class Uri(val value: String) : ClickSoundSource()
}
