package com.vsevolodganin.clicktrack.utils.collection.immutable

fun <T> List<T>.move(
    fromIndex: Int,
    toIndex: Int,
): List<T> {
    if (fromIndex == toIndex || fromIndex !in indices) return this
    return toMutableList().apply {
        add(toIndex.coerceIn(indices), removeAt(fromIndex))
    }
}
