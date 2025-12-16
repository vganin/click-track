package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.soundlibrary.DocumentMetadataHelper
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(MainControllerScope::class)
@Inject
class DummyDocumentMetadataHelperImpl : DocumentMetadataHelper {
    override fun isAccessible(uri: String): Boolean = true

    override fun hasReadPermission(uri: String): Boolean = true

    override fun getDisplayName(uri: String): String? = null
}
