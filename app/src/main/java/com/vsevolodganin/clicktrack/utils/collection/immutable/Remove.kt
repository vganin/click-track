package com.vsevolodganin.clicktrack.utils.collection.immutable

fun <T> List<T>.remove(index: Int): List<T> {
    if (index !in indices) return this
    return toMutableList().apply {
        removeAt(index)
    }
}
