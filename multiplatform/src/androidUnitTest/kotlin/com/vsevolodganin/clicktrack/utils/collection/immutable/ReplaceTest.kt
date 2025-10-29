package com.vsevolodganin.clicktrack.utils.collection.immutable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class ReplaceTest {

    @Test
    fun `replace should transform element at valid index`() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.replace(2) { it * 10 }

        assertEquals(listOf(1, 2, 30, 4, 5), result)
    }

    @Test
    fun `replace should transform first element`() {
        val list = listOf("a", "b", "c")
        val result = list.replace(0) { it.uppercase() }

        assertEquals(listOf("A", "b", "c"), result)
    }

    @Test
    fun `replace should transform last element`() {
        val list = listOf("a", "b", "c")
        val result = list.replace(2) { it.uppercase() }

        assertEquals(listOf("a", "b", "C"), result)
    }

    @Test
    fun `replace should return original list when index is negative`() {
        val list = listOf(1, 2, 3)
        val result = list.replace(-1) { it * 2 }

        assertEquals(list, result)
    }

    @Test
    fun `replace should return original list when index is out of bounds`() {
        val list = listOf(1, 2, 3)
        val result = list.replace(10) { it * 2 }

        assertEquals(list, result)
    }

    @Test
    fun `replace should return original list when index equals list size`() {
        val list = listOf(1, 2, 3)
        val result = list.replace(3) { it * 2 }

        assertEquals(list, result)
    }

    @Test
    fun `replace should create a new list instance`() {
        val list = listOf(1, 2, 3)
        val result = list.replace(1) { it + 10 }

        assertNotSame(list, result)
    }

    @Test
    fun `replace on single element list should transform that element`() {
        val list = listOf(42)
        val result = list.replace(0) { it * 2 }

        assertEquals(listOf(84), result)
    }

    @Test
    fun `replace should allow changing element type through transformation`() {
        val list = listOf(1, 2, 3)
        val result = list.replace(1) { it } // Keep same type for this test

        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun `replace should work with complex transformations`() {
        data class Person(val name: String, val age: Int)
        val list = listOf(
            Person("Alice", 25),
            Person("Bob", 30),
            Person("Charlie", 35),
        )
        val result = list.replace(1) { it.copy(age = it.age + 1) }

        assertEquals(
            listOf(
                Person("Alice", 25),
                Person("Bob", 31),
                Person("Charlie", 35),
            ),
            result,
        )
    }

    @Test
    fun `replace should work with null values`() {
        val list = listOf(1, null, 3)
        val result = list.replace(1) { 2 }

        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun `replace on empty list should return empty list`() {
        val list = emptyList<Int>()
        val result = list.replace(0) { it * 2 }

        assertEquals(emptyList<Int>(), result)
    }

    @Test
    fun `replace should handle identity transformation`() {
        val list = listOf("a", "b", "c")
        val result = list.replace(1) { it }

        assertEquals(list, result)
    }

    @Test
    fun `replace should allow replacing with constant value`() {
        val list = listOf(1, 2, 3, 4, 5)
        val result = list.replace(2) { 999 }

        assertEquals(listOf(1, 2, 999, 4, 5), result)
    }
}
