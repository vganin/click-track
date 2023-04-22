package com.vsevolodganin.clicktrack.utils.collection.sequence

fun <T> Sequence<T>.prefetch(n: Int): Sequence<T> {
    val prefetched = ArrayList<T>(n)
    val iterator = iterator()
    repeat(n) {
        if (iterator.hasNext()) {
            prefetched += iterator.next()
        }
    }
    return sequence {
        yieldAll(prefetched)
        yieldAll(iterator)
    }.constrainOnce()
}
