package com.vsevolodganin.clicktrack.utils.decompose

import com.arkivanov.essenty.statekeeper.SerializableContainer
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import platform.Foundation.NSCoder
import platform.Foundation.NSString
import platform.Foundation.decodeTopLevelObjectOfClass
import platform.Foundation.encodeObject

fun createStateKeeperDispatcher(savedState: SerializableContainer? = null) = StateKeeperDispatcher(savedState)

fun save(coder: NSCoder, state: SerializableContainer) {
    coder.encodeObject(`object` = json.encodeToString(SerializableContainer.serializer(), state), forKey = STATE_KEY)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun restore(coder: NSCoder): SerializableContainer? {
    return (coder.decodeTopLevelObjectOfClass(aClass = NSString, forKey = STATE_KEY, error = null) as String?)?.let {
        try {
            json.decodeFromString(SerializableContainer.serializer(), it)
        } catch (e: Exception) {
            println("Failed to restore state: $e")
            null
        }
    }
}

private const val STATE_KEY = "state"

private val json = Json {
    allowStructuredMapKeys = true
}
