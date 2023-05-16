package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.soundlibrary.DocumentMetadataHelper
import me.tatarka.inject.annotations.Inject

@Inject
class DummyDocumentMetadataHelperImpl : DocumentMetadataHelper {
    override fun isAccessible(uri: String): Boolean = true
    override fun hasReadPermission(uri: String): Boolean = true
    override fun getDisplayName(uri: String): String? = null
}
