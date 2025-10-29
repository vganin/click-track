package com.vsevolodganin.clicktrack.utils.collection.sequence

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PrefetchTest {

    @Test
    fun `prefetch should fetch specified number of elements upfront`() {
        var fetchCount = 0
        val sequence = generateSequence {
            fetchCount++
            fetchCount
        }

        val prefetched = sequence.prefetch(3)
        // Creating the prefetch sequence should fetch 3 elements
        assertEquals(3, fetchCount)

        // Consuming the sequence should use prefetched elements first
        val result = prefetched.take(5).toList()
        assertEquals(listOf(1, 2, 3, 4, 5), result)
    }

    @Test
    fun `prefetch should handle sequences shorter than prefetch count`() {
        val sequence = sequenceOf(1, 2, 3)
        val prefetched = sequence.prefetch(10)

        val result = prefetched.toList()
        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun `prefetch with zero should not prefetch any elements`() {
        var fetchCount = 0
        val sequence = generateSequence {
            fetchCount++
            fetchCount
        }

        val prefetched = sequence.prefetch(0)
        assertEquals(0, fetchCount)

        val result = prefetched.take(3).toList()
        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun `prefetch on empty sequence should return empty sequence`() {
        val sequence = emptySequence<Int>()
        val prefetched = sequence.prefetch(5)

        val result = prefetched.toList()
        assertEquals(emptyList<Int>(), result)
    }

    @Test
    fun `prefetch should work with single element sequence`() {
        val sequence = sequenceOf(42)
        val prefetched = sequence.prefetch(1)

        val result = prefetched.toList()
        assertEquals(listOf(42), result)
    }

    @Test
    fun `prefetch should not consume more than specified elements`() {
        var consumedCount = 0
        val sequence = generateSequence {
            consumedCount++
            consumedCount
        }

        val prefetched = sequence.prefetch(5)
        // Should have consumed exactly 5 elements during prefetch
        assertEquals(5, consumedCount)
    }

    @Test
    fun `prefetch should work with different data types`() {
        val sequence = sequenceOf("a", "b", "c", "d", "e")
        val prefetched = sequence.prefetch(3)

        val result = prefetched.toList()
        assertEquals(listOf("a", "b", "c", "d", "e"), result)
    }

    @Test
    fun `prefetch should maintain sequence order`() {
        val sequence = (1..10).asSequence()
        val prefetched = sequence.prefetch(5)

        val result = prefetched.toList()
        assertEquals((1..10).toList(), result)
    }

    @Test
    fun `prefetch should be constrained to single use`() {
        val sequence = sequenceOf(1, 2, 3, 4, 5)
        val prefetched = sequence.prefetch(2)

        // First iteration should work
        val firstResult = prefetched.toList()
        assertEquals(listOf(1, 2, 3, 4, 5), firstResult)

        // Second iteration should fail due to constrainOnce()
        var exceptionThrown = false
        try {
            prefetched.toList()
        } catch (e: IllegalStateException) {
            exceptionThrown = true
        }

        assertTrue(exceptionThrown, "Expected IllegalStateException for second iteration")
    }

    @Test
    fun `prefetch with large count should prefetch all available elements`() {
        val sequence = (1..5).asSequence()
        val prefetched = sequence.prefetch(100)

        val result = prefetched.toList()
        assertEquals(listOf(1, 2, 3, 4, 5), result)
    }

    @Test
    fun `prefetch should handle lazy sequence evaluation correctly`() {
        val evaluatedIndices = mutableListOf<Int>()
        val sequence = (1..10).asSequence().map {
            evaluatedIndices.add(it)
            it * 2
        }

        val prefetched = sequence.prefetch(3)
        // First 3 should be evaluated immediately
        assertEquals(listOf(1, 2, 3), evaluatedIndices)

        // Taking more should evaluate remaining elements
        val result = prefetched.take(7).toList()
        assertEquals(listOf(2, 4, 6, 8, 10, 12, 14), result)
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7), evaluatedIndices)
    }

    @Test
    fun `prefetch should work with infinite sequences`() {
        val infiniteSequence = generateSequence(0) { it + 1 }
        val prefetched = infiniteSequence.prefetch(5)

        val result = prefetched.take(10).toList()
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), result)
    }
}
