package com.vsevolodganin.clicktrack.utils.collection

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RoundRobinTest {

    @Test
    fun `toRoundRobin on Iterable should repeat elements infinitely`() {
        val source = listOf(1, 2, 3)
        val roundRobin = source.toRoundRobin()
        val result = roundRobin.iterator().let { iterator ->
            buildList {
                repeat(10) {
                    add(iterator.next())
                }
            }
        }

        assertEquals(listOf(1, 2, 3, 1, 2, 3, 1, 2, 3, 1), result)
    }

    @Test
    fun `toRoundRobin on Iterable should always have next element`() {
        val source = listOf(1, 2, 3)
        val roundRobin = source.toRoundRobin()
        val iterator = roundRobin.iterator()

        repeat(100) {
            assertTrue(iterator.hasNext())
            iterator.next()
        }
    }

    @Test
    fun `toRoundRobin on Iterable should fail when source is empty`() {
        val source = emptyList<Int>()

        assertFailsWith<IllegalArgumentException> {
            source.toRoundRobin()
        }
    }

    @Test
    fun `toRoundRobin on Iterable with single element should repeat that element`() {
        val source = listOf(42)
        val roundRobin = source.toRoundRobin()
        val result = roundRobin.iterator().let { iterator ->
            buildList {
                repeat(5) {
                    add(iterator.next())
                }
            }
        }

        assertEquals(listOf(42, 42, 42, 42, 42), result)
    }

    @Test
    fun `toRoundRobin on Sequence should repeat elements infinitely`() {
        val source = sequenceOf(1, 2, 3)
        val roundRobin = source.toRoundRobin()
        val result = roundRobin.take(10).toList()

        assertEquals(listOf(1, 2, 3, 1, 2, 3, 1, 2, 3, 1), result)
    }

    @Test
    fun `toRoundRobin on Sequence with single element should repeat that element`() {
        val source = sequenceOf("test")
        val roundRobin = source.toRoundRobin()
        val result = roundRobin.take(5).toList()

        assertEquals(listOf("test", "test", "test", "test", "test"), result)
    }

    @Test
    fun `toRoundRobin on empty Sequence should produce infinite empty cycles`() {
        val source = emptySequence<Int>()
        val roundRobin = source.toRoundRobin()
        // Taking from an infinite sequence of empty cycles should complete instantly
        val result = roundRobin.take(0).toList()

        assertEquals(emptyList<Int>(), result)
    }

    @Test
    fun `toRoundRobin on Iterable should create new iterator for each iteration`() {
        val source = listOf("a", "b", "c")
        val roundRobin = source.toRoundRobin()
        
        val firstIterator = roundRobin.iterator()
        val firstResults = buildList {
            repeat(4) {
                add(firstIterator.next())
            }
        }
        
        val secondIterator = roundRobin.iterator()
        val secondResults = buildList {
            repeat(4) {
                add(secondIterator.next())
            }
        }

        assertEquals(listOf("a", "b", "c", "a"), firstResults)
        assertEquals(listOf("a", "b", "c", "a"), secondResults)
    }
}
