package com.vsevolodganin.clicktrack.lib.utils.collection

public class RoundRobinIterable<T>(private val source: Iterable<T>) {

    init {
        require(source.any()) { "Source should have at least one element" }
    }

    private var iterator = source.iterator()

    public operator fun next(): T {
        if (!iterator.hasNext()) {
            iterator = source.iterator()
        }
        return iterator.next()
    }
}

public fun <T> Iterable<T>.toRoundRobin(): RoundRobinIterable<T> = RoundRobinIterable(this)
