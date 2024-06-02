package com.vsevolodganin.clicktrack.utils.resources

import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.KSerializer

expect class FileResourceSerializer : KSerializer<FileResource>
