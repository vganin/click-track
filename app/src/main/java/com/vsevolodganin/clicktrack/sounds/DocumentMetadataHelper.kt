package com.vsevolodganin.clicktrack.sounds

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import javax.inject.Inject
import timber.log.Timber

class DocumentMetadataHelper @Inject constructor(private val contentResolver: ContentResolver) {

    fun hasReadPermission(uri: String): Boolean {
        return contentResolver.persistedUriPermissions.any { permission ->
            permission.uri.toString() == uri && permission.isReadPermission
        }
    }

    fun getDisplayName(uri: String): String? {
        val compiledUri = Uri.parse(uri)

        return try {
            contentResolver.query(compiledUri, null, null, null, null, null)?.use {
                if (it.moveToFirst()) {
                    return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                } else {
                    null
                }
            } ?: compiledUri.lastPathSegment
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to get display name for URI: $uri")
            null
        }
    }
}
