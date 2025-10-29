package com.vsevolodganin.clicktrack.utils.collection.immutable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class MoveTest {

    @Test
    fun `move should move element forward in the list`() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.move(fromIndex = 1, toIndex = 3)

        assertEquals(listOf(1, 3, 4, 2, 5), result)
    }

    @Test
    fun `move should move element backward in the list`() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.move(fromIndex = 3, toIndex = 1)

        assertEquals(listOf(1, 4, 2, 3, 5), result)
    }

    @Test
    fun `move should move first element to end`() {
        val list = listOf("a", "b", "c", "d")
        val result = list.move(fromIndex = 0, toIndex = 3)

        assertEquals(listOf("b", "c", "d", "a"), result)
    }

    @Test
    fun `move should move last element to beginning`() {
        val list = listOf("a", "b", "c", "d")
        val result = list.move(fromIndex = 3, toIndex = 0)

        assertEquals(listOf("d", "a", "b", "c"), result)
    }

    @Test
    fun `move should return original list when fromIndex equals toIndex`() {
        val list = listOf(1, 2, 3, 4)
        val result = list.move(fromIndex = 2, toIndex = 2)

        assertEquals(list, result)
    }

    @Test
    fun `move should return original list when fromIndex is negative`() {
        val list = listOf(1, 2, 3)
        val result = list.move(fromIndex = -1, toIndex = 1)

        assertEquals(list, result)
    }

    @Test
    fun `move should return original list when fromIndex is out of bounds`() {
        val list = listOf(1, 2, 3)
        val result = list.move(fromIndex = 5, toIndex = 1)

        assertEquals(list, result)
    }

    @Test
    fun `move should coerce toIndex to valid range when too large`() {
        val list = listOf(1, 2, 3, 4)
        val result = list.move(fromIndex = 0, toIndex = 10)

        // toIndex is coerced to last valid index
        assertEquals(listOf(2, 3, 4, 1), result)
    }

    @Test
    fun `move should coerce toIndex to valid range when negative`() {
        val list = listOf(1, 2, 3, 4)
        val result = list.move(fromIndex = 3, toIndex = -5)

        // toIndex is coerced to 0
        assertEquals(listOf(4, 1, 2, 3), result)
    }

    @Test
    fun `move should create a new list instance`() {
        val list = listOf(1, 2, 3)
        val result = list.move(fromIndex = 0, toIndex = 2)

        assertNotSame(list, result)
    }

    @Test
    fun `move on single element list should return same list`() {
        val list = listOf(42)
        val result = list.move(fromIndex = 0, toIndex = 0)

        assertEquals(list, result)
    }

    @Test
    fun `move should handle adjacent elements`() {
        val list = listOf(1, 2, 3, 4)
        val result = list.move(fromIndex = 1, toIndex = 2)

        assertEquals(listOf(1, 3, 2, 4), result)
    }

    @Test
    fun `move should work with different data types`() {
        val list = listOf("alpha", "beta", "gamma", "delta")
        val result = list.move(fromIndex = 2, toIndex = 0)

        assertEquals(listOf("gamma", "alpha", "beta", "delta"), result)
    }

    @Test
    fun `move in two element list should swap elements`() {
        val list = listOf("first", "second")
        val result = list.move(fromIndex = 0, toIndex = 1)

        assertEquals(listOf("second", "first"), result)
    }
}
