package com.vsevolodganin.clicktrack.lib.utils.collection

public fun <T> Iterable<T>.toRoundRobin(): Iterable<T> = RoundRobinIterable(this)

public fun <T> Sequence<T>.toRoundRobin(): Sequence<T> = sequence {
    while (true) {
        yieldAll(this@toRoundRobin)
    }
}

private class RoundRobinIterable<T>(private val source: Iterable<T>) : Iterable<T> {

    init {
        require(source.any()) { "Source should have at least one element" }
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {

        private var iterator = source.iterator()

        override fun hasNext(): Boolean = true

        override fun next(): T {
            if (!iterator.hasNext()) {
                iterator = source.iterator()
            }
            return iterator.next()
        }
    }
}
