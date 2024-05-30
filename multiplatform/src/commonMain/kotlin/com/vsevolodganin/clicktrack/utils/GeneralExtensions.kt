package com.vsevolodganin.clicktrack.utils

inline fun <reified T : Any?> Any?.optionalCast(): T? = this as? T

inline fun <reified T : Any?> Any?.cast(): T = this as T

fun <T> grabIf(
    condition: Boolean,
    producer: () -> T,
): T? {
    return if (condition) {
        producer()
    } else {
        null
    }
}
