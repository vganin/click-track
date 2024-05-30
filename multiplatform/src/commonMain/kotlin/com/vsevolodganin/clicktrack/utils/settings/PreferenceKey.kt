package com.vsevolodganin.clicktrack.utils.settings

import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KSuspendFunction2

sealed interface PreferenceKey<T> {
    val name: kotlin.String
    val flow: FlowSettings.() -> Flow<T?>
    val get: suspend FlowSettings.() -> T?
    val put: suspend FlowSettings.(T?) -> Unit

    class Boolean(override val name: kotlin.String) : PreferenceKey<kotlin.Boolean> {
        override val flow: FlowSettings.() -> Flow<kotlin.Boolean?> = { getBooleanOrNullFlow(name) }
        override val get: suspend FlowSettings.() -> kotlin.Boolean? = { getBooleanOrNull(name) }
        override val put: suspend FlowSettings.(kotlin.Boolean?) -> Unit = { removeOrPut(name, it, ::putBoolean) }
    }

    class Int(override val name: kotlin.String) : PreferenceKey<kotlin.Int> {
        override val flow: FlowSettings.() -> Flow<kotlin.Int?> = { getIntOrNullFlow(name) }
        override val get: suspend FlowSettings.() -> kotlin.Int? = { getIntOrNull(name) }
        override val put: suspend FlowSettings.(kotlin.Int?) -> Unit = { removeOrPut(name, it, ::putInt) }
    }

    class Long(override val name: kotlin.String) : PreferenceKey<kotlin.Long> {
        override val flow: FlowSettings.() -> Flow<kotlin.Long?> = { getLongOrNullFlow(name) }
        override val get: suspend FlowSettings.() -> kotlin.Long? = { getLongOrNull(name) }
        override val put: suspend FlowSettings.(kotlin.Long?) -> Unit = { removeOrPut(name, it, ::putLong) }
    }

    class String(override val name: kotlin.String) : PreferenceKey<kotlin.String> {
        override val flow: FlowSettings.() -> Flow<kotlin.String?> = { getStringOrNullFlow(name) }
        override val get: suspend FlowSettings.() -> kotlin.String? = { getStringOrNull(name) }
        override val put: suspend FlowSettings.(kotlin.String?) -> Unit = { removeOrPut(name, it, ::putString) }
    }
}

private suspend inline fun <reified T : Any> FlowSettings.removeOrPut(
    key: String,
    value: T?,
    put: KSuspendFunction2<String, T, Unit>,
): Unit =
    if (value == null) {
        remove(key)
    } else {
        put.invoke(key, value)
    }
