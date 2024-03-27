package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.utils.parcelable.Parcelable
import com.vsevolodganin.clicktrack.utils.parcelable.Parcelize
import com.vsevolodganin.clicktrack.utils.parcelable.TypeParceler
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
