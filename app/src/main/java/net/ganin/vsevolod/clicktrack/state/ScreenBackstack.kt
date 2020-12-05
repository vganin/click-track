package net.ganin.vsevolod.clicktrack.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenBackstack(val screens: List<Screen>) : Parcelable

fun ScreenBackstack.frontScreenPosition(): Int {
    return screens.lastIndex
}

fun ScreenBackstack.frontScreen(): Screen? {
    return screens.lastOrNull()
}

fun ScreenBackstack.pop(): ScreenBackstack {
    return ScreenBackstack(screens.dropLast(1))
}

fun ScreenBackstack.push(screen: Screen): ScreenBackstack {
    return ScreenBackstack(screens + screen)
}

fun ScreenBackstack.replaceCurrentScreen(mapper: (Screen) -> Screen): ScreenBackstack {
    if (screens.isEmpty()) return this
    val mutableScreens = screens.toMutableList()
    mutableScreens[screens.lastIndex] = mapper.invoke(mutableScreens.last())
    return ScreenBackstack(mutableScreens)
}
