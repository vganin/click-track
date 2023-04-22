package com.vsevolodganin.clicktrack.utils.resources

import android.os.Parcel
import com.arkivanov.essenty.parcelable.Parceler
import dev.icerock.moko.resources.FileResource

actual class FileResourceParceler : Parceler<FileResource> {

    override fun create(parcel: Parcel): FileResource {
        return FileResource(parcel.readInt())
    }

    override fun FileResource.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(rawResId)
    }
}
