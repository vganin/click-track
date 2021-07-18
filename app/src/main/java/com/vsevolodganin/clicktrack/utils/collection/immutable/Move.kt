package com.vsevolodganin.clicktrack.utils.collection.immutable

fun <T> List<T>.move(fromIndex: Int, toIndex: Int): List<T> {
    return toMutableList().apply {
        add(toIndex, this[fromIndex].also { removeAt(fromIndex) })
    }
}
