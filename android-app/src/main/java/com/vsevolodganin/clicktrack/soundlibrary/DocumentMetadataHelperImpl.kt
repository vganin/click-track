package com.vsevolodganin.clicktrack.soundlibrary

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.vsevolodganin.clicktrack.utils.log.Logger
import me.tatarka.inject.annotations.Inject

@Inject
class DocumentMetadataHelperImpl(
    private val contentResolver: ContentResolver,
    private val logger: Logger,
) : DocumentMetadataHelper {
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
            logger.logError(TAG, "Failed to get display name for URI: $uri", e)
            null
        }
    }

    private companion object {
        const val TAG = "DocumentMetadataHelperImpl"
    }
}
