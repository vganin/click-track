package com.vsevolodganin.clicktrack.redux

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenBackstack(
    val frontScreen: Screen,
    val restScreens: List<Screen>,
) : Parcelable {
    val screens: List<Screen> get() = restScreens + frontScreen
}

@Suppress("FunctionName") // Factory function
fun ScreenBackstack(screens: List<Screen>): ScreenBackstack? {
    return if (screens.isEmpty()) {
        null
    } else {
        ScreenBackstack(
            frontScreen = screens.last(),
            restScreens = screens.dropLast(1),
        )
    }
}

fun ScreenBackstack.pop(): ScreenBackstack {
    return ScreenBackstack(restScreens) ?: this
}

fun ScreenBackstack.pop(predicate: (Screen) -> Boolean): ScreenBackstack {
    return ScreenBackstack(screens.dropLastWhile { !predicate(it) }.dropLast(1)) ?: this
}

fun ScreenBackstack.resetTo(screen: Screen): ScreenBackstack {
    return copy(
        frontScreen = screen,
        restScreens = listOf(Screen.ClickTrackList)
    )
}

fun ScreenBackstack.push(screen: Screen): ScreenBackstack {
    return copy(
        frontScreen = screen,
        restScreens = screens
    )
}
