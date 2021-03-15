package com.vsevolodganin.clicktrack.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenBackstack(val screens: List<Screen>, val drawerState: DrawerScreenState) : Parcelable

fun ScreenBackstack.frontScreenPosition(): Int {
    return screens.lastIndex
}

fun ScreenBackstack.frontScreen(): Screen? {
    return screens.lastOrNull()
}

fun ScreenBackstack.pop(): ScreenBackstack {
    return copy(screens = screens.dropLast(1))
}

fun ScreenBackstack.pushOrIgnore(screen: Screen): ScreenBackstack {
    return pushOrIgnore { screen }
}

fun ScreenBackstack.pushOrIgnore(screenFactory: () -> Screen): ScreenBackstack {
    val current = frontScreen()
    val next = screenFactory()
    return if (current?.javaClass == next.javaClass) {
        this
    } else {
        copy(screens = screens + screenFactory())
    }
}

fun ScreenBackstack.pushOrReplace(screen: Screen): ScreenBackstack {
    return pushOrReplace { screen }
}

fun ScreenBackstack.pushOrReplace(screenFactory: () -> Screen): ScreenBackstack {
    val current = frontScreen()
    val next = screenFactory()
    return if (current?.javaClass == next.javaClass) {
        copy(screens = screens.dropLast(1) + screenFactory())
    } else {
        copy(screens = screens + screenFactory())
    }
}

fun ScreenBackstack.replaceCurrentScreen(mapper: (Screen) -> Screen): ScreenBackstack {
    if (screens.isEmpty()) return this
    val mutableScreens = screens.toMutableList()
    mutableScreens[screens.lastIndex] = mapper.invoke(mutableScreens.last())
    return copy(screens = mutableScreens)
}
