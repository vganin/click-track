package com.vsevolodganin.clicktrack.utils.collection.immutable

fun <T> Iterable<T>.replace(index: Int, map: (T) -> T): List<T> {
    return toMutableList().apply {
        this[index] = map(this[index])
    }
}
