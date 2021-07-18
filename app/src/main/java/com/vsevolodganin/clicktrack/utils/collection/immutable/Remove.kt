package com.vsevolodganin.clicktrack.utils.collection.immutable

fun <T> List<T>.remove(index: Int): List<T> {
    return toMutableList().apply {
        removeAt(index)
    }
}
