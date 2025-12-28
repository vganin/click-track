package com.vsevolodganin.clicktrack.soundlibrary

import dev.zacsweers.metro.Inject
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL

@Inject
actual class DocumentMetadataHelper {

    actual fun isAccessible(uri: String): Boolean {
        val path = NSURL.URLWithString(uri)?.path ?: return false
        return NSFileManager.defaultManager.fileExistsAtPath(path)
    }

    actual fun hasReadPermission(uri: String): Boolean {
        val path = NSURL.URLWithString(uri)?.path ?: return false
        return NSFileManager.defaultManager.isReadableFileAtPath(path)
    }

    actual fun getDisplayName(uri: String): String? {
        return NSURL.URLWithString(uri)?.lastPathComponent
    }
}
