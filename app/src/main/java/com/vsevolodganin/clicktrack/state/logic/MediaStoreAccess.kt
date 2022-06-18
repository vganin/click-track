package com.vsevolodganin.clicktrack.state.logic

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import javax.inject.Inject

@Suppress("DEPRECATION") // Using DATA column to access file path because MediaMuxer can't work with FileDescriptor on earlier Androids
class MediaStoreAccess @Inject constructor(
    private val resolver: ContentResolver,
) {
    fun addFile(file: File) {
        val audioCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val fileDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, file.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Audio.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MUSIC}${File.separator}$CATEGORY_NAME")
            }
        }

        val fileUri = resolver.insert(audioCollection, fileDetails) ?: return

        resolver.openOutputStream(fileUri)?.use {
            file.inputStream().copyTo(it)
        }
    }

    private companion object {
        const val CATEGORY_NAME = "ClickTrack"
    }
}
