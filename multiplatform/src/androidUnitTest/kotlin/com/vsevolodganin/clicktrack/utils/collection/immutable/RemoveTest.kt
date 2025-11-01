package com.vsevolodganin.clicktrack.utils.collection.immutable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class RemoveTest {

    @Test
    fun `remove should remove element at valid index`() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.remove(2)

        assertEquals(listOf(1, 2, 4, 5), result)
    }

    @Test
    fun `remove should remove first element`() {
        val list = listOf("a", "b", "c")
        val result = list.remove(0)

        assertEquals(listOf("b", "c"), result)
    }

    @Test
    fun `remove should remove last element`() {
        val list = listOf("a", "b", "c")
        val result = list.remove(2)

        assertEquals(listOf("a", "b"), result)
    }

    @Test
    fun `remove should return original list when index is negative`() {
        val list = listOf(1, 2, 3)
        val result = list.remove(-1)

        assertEquals(list, result)
    }

    @Test
    fun `remove should return original list when index is out of bounds`() {
        val list = listOf(1, 2, 3)
        val result = list.remove(10)

        assertEquals(list, result)
    }

    @Test
    fun `remove should return original list when index equals list size`() {
        val list = listOf(1, 2, 3)
        val result = list.remove(3)

        assertEquals(list, result)
    }

    @Test
    fun `remove should create a new list instance`() {
        val list = listOf(1, 2, 3)
        val result = list.remove(1)

        assertNotSame(list, result)
    }

    @Test
    fun `remove on single element list should return empty list`() {
        val list = listOf(42)
        val result = list.remove(0)

        assertEquals(emptyList<Int>(), result)
    }

    @Test
    fun `remove on empty list with any index should return empty list`() {
        val list = emptyList<String>()
        val result = list.remove(0)

        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `remove should work with different data types`() {
        val list = listOf("apple", "banana", "cherry", "date")
        val result = list.remove(1)

        assertEquals(listOf("apple", "cherry", "date"), result)
    }
}
