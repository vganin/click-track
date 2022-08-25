package com.vsevolodganin.clicktrack.common

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.Reusable
import java.io.File
import javax.inject.Inject

@Reusable
class MediaStoreAccess @Inject constructor(private val resolver: ContentResolver) {

    fun addAudioFile(file: File): Uri? {
        val audioCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val fileDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, file.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Audio.Media.IS_PENDING, 1)
                put(MediaStore.Audio.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MUSIC}${File.separator}$CATEGORY_NAME")
            } else {
                // Workarounds NullPointerException in MediaProvider on some older Androids: https://stackoverflow.com/a/72678037/4707823
                val targetFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), file.name)
                put(MediaStore.Audio.Media.DATA, targetFile.absolutePath)
            }
        }

        val accessUri = resolver.insert(audioCollection, fileDetails) ?: return null

        resolver.openOutputStream(accessUri)?.use {
            file.inputStream().copyTo(it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fileDetails.clear()
            fileDetails.put(MediaStore.Audio.Media.IS_PENDING, 0)
            resolver.update(accessUri, fileDetails, null, null)
        }

        return accessUri
    }

    private companion object {
        const val CATEGORY_NAME = "ClickTrack"
    }
}
