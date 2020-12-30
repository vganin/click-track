package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

fun <T> observableMutableStateOf(value: T) = ObservableMutableState(value)

class ObservableMutableState<T>(value: T) : MutableState<T> {

    private val impl = mutableStateOf(value)
    private val listeners = mutableSetOf<(T) -> Unit>()

    fun observe(listener: (T) -> Unit): ObservableMutableState<T> {
        listeners += listener
        return this
    }

    override var value: T
        get() = impl.value
        set(value) {
            impl.value = value
            listeners.forEach { it.invoke(value) }
        }

    override fun component1(): T = impl.component1()
    override fun component2(): (T) -> Unit = impl.component2()
}

fun <T> List<T>.toObservableMutableStateList() = ObservableMutableStateList(this)

class ObservableMutableStateList<T> private constructor(
    private val impl: SnapshotStateList<T>
) : MutableList<T> by impl {

    constructor(list: List<T>) : this(list.toMutableStateList())

    private val listeners = mutableSetOf<(List<T>) -> Unit>()

    fun observe(listener: (List<T>) -> Unit): ObservableMutableStateList<T> {
        listeners += listener
        return this
    }

    private fun <T> T.alsoNotifyListeners(): T {
        return also {
            val updatedList = impl
            listeners.forEach { it.invoke(updatedList) }
        }
    }

    override fun add(element: T): Boolean = impl.add(element).alsoNotifyListeners()

    override fun add(index: Int, element: T) = impl.add(index, element).alsoNotifyListeners()

    override fun addAll(index: Int, elements: Collection<T>): Boolean = impl.addAll(index, elements).alsoNotifyListeners()

    override fun addAll(elements: Collection<T>): Boolean = impl.addAll(elements).alsoNotifyListeners()

    override fun clear() = impl.clear().alsoNotifyListeners()

    override fun remove(element: T): Boolean = impl.remove(element).alsoNotifyListeners()

    override fun removeAll(elements: Collection<T>): Boolean = impl.removeAll(elements).alsoNotifyListeners()

    override fun removeAt(index: Int): T = impl.removeAt(index).alsoNotifyListeners()

    override fun retainAll(elements: Collection<T>): Boolean = impl.retainAll(elements).alsoNotifyListeners()

    override fun set(index: Int, element: T): T = impl.set(index, element).alsoNotifyListeners()
}
