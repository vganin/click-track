package com.vsevolodganin.clicktrack.utils.resources

import android.os.Parcel
import dev.icerock.moko.resources.FileResource
import kotlinx.parcelize.Parceler

actual class FileResourceParceler : Parceler<FileResource> {

    override fun create(parcel: Parcel): FileResource {
        return FileResource(parcel.readInt())
    }

    override fun FileResource.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(rawResId)
    }
}
