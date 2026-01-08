package com.vsevolodganin.clicktrack.utils.resources

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
suspend fun DrawableResource.toUIImage(): UIImage {
    val bytes = getDrawableResourceBytes(
        environment = getSystemResourceEnvironment(),
        resource = this,
    )
    val nsData = bytes.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = bytes.size.toULong(),
        )
    }
    return requireNotNull(UIImage.imageWithData(nsData)) {
        "Failed to load image from drawable resource $this"
    }
}
