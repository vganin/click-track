package com.vsevolodganin.clicktrack.soundlibrary

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import me.tatarka.inject.annotations.Inject
import timber.log.Timber

@Inject
class DocumentMetadataHelperImpl(private val contentResolver: ContentResolver) : DocumentMetadataHelper {

    override fun isAccessible(uri: String): Boolean {
        return try {
            contentResolver.query(Uri.parse(uri), null, null, null, null, null)?.use {
                it.moveToFirst()
            } ?: false
        } catch (e: SecurityException) {
            false
        }
    }

    override fun hasReadPermission(uri: String): Boolean {
        return contentResolver.persistedUriPermissions.any { permission ->
            permission.uri.toString() == uri && permission.isReadPermission
        }
    }

    override fun getDisplayName(uri: String): String? {
        val parsedUri = Uri.parse(uri)

        return try {
            contentResolver.query(parsedUri, null, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).takeIf { it >= 0 } ?: return null
                    return cursor.getString(columnIndex)
                } else {
                    null
                }
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to get display name for URI: $uri")
            null
        }
    }
}
