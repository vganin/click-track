package com.vsevolodganin.clicktrack.state.screen

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenBackstack(val screens: List<Screen>, val drawerState: DrawerScreenState) : Parcelable

fun List<Screen>.frontScreenPosition(): Int {
    return lastIndex
}

fun List<Screen>.frontScreen(): Screen? {
    return lastOrNull()
}

fun List<Screen>.pop(): List<Screen> {
    return dropLast(1)
}

fun List<Screen>.pushOrIgnore(screenFactory: () -> Screen): List<Screen> {
    val current = frontScreen()
    val next = screenFactory()
    return if (current?.javaClass == next.javaClass) {
        this
    } else {
        this + screenFactory()
    }
}

fun List<Screen>.pushOrReplace(screen: Screen): List<Screen> {
    return pushOrReplace { screen }
}

fun List<Screen>.pushOrReplace(screenFactory: () -> Screen): List<Screen> {
    val current = frontScreen()
    val next = screenFactory()
    return if (current?.javaClass == next.javaClass) {
        dropLast(1) + screenFactory()
    } else {
        this + screenFactory()
    }
}

fun List<Screen>.replaceCurrentScreen(mapper: (Screen) -> Screen): List<Screen> {
    if (isEmpty()) return this
    val mutableScreens = toMutableList()
    mutableScreens[lastIndex] = mapper.invoke(mutableScreens.last())
    return mutableScreens
}
