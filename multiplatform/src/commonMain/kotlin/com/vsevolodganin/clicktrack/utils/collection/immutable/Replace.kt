package com.vsevolodganin.clicktrack.utils.collection.immutable

fun <T> List<T>.replace(index: Int, map: (T) -> T): List<T> {
    if (index !in indices) return this
    return toMutableList().apply {
        this[index] = map(this[index])
    }
}
