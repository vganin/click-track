package com.vsevolodganin.clicktrack.soundlibrary

interface DocumentMetadataHelper {
    fun isAccessible(uri: String): Boolean

    fun hasReadPermission(uri: String): Boolean

    fun getDisplayName(uri: String): String?
}
