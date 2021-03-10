package com.vsevolodganin.clicktrack.sounds

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import javax.inject.Inject

class DocumentMetadataHelper @Inject constructor(private val contentResolver: ContentResolver) {

    fun getDisplayName(uri: String): String? {
        val compiledUri = Uri.parse(uri)
        val cursor: Cursor? = contentResolver.query(
            compiledUri, null, null, null, null, null)

        return cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            } else {
                null
            }
        } ?: compiledUri.lastPathSegment
    }
}
