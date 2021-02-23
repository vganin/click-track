@file:Suppress("UNCHECKED_CAST") // All casts are necessary and should be safe

package com.vsevolodganin.clicktrack.utils.android.sharedprefs

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface SharedPreferencesProperty<T> {
    val getter: SharedPreferences.() -> T
    val setter: SharedPreferences.(T) -> Unit
    val flow: SharedPreferences.() -> Flow<T>
}

sealed class KeyedSharedPreferencesPropertyImpl<T>(
    val key: String,
    override val getter: SharedPreferences.() -> T,
    override val setter: SharedPreferences.(T) -> Unit,
) : SharedPreferencesProperty<T> {
    override val flow: SharedPreferences.() -> Flow<T> get() = { flowFrom(key, getter) }
}

class IntConvertibleProperty<T : Any?>(
    key: String,
    defaultValue: T,
    toInt: T.() -> Int,
    fromInt: Int.() -> T,
) : KeyedSharedPreferencesPropertyImpl<T>(
    key = key,
    getter = { getInt(key, defaultValue.toInt()).fromInt() },
    setter = { edit().putInt(key, it.toInt()).apply() },
)

class StringConvertibleProperty<T : Any?>(
    key: String,
    defaultValue: T,
    toString: T.() -> String,
    fromString: String.() -> T,
) : KeyedSharedPreferencesPropertyImpl<T>(
    key = key,
    getter = { getString(key, defaultValue.toString())?.fromString() as T },
    setter = { edit().putString(key, it.toString()).apply() },
)

private fun <T> SharedPreferences.flowFrom(key: String, getter: SharedPreferences.() -> T): Flow<T> {
    return callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sp, changed ->
            if (changed == key) {
                try {
                    sendBlocking(sp.getter())
                } catch (t: Throwable) {
                    // Ignore
                }
            }
        }
        registerOnSharedPreferenceChangeListener(listener)
        send(getter())
        awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }
}
