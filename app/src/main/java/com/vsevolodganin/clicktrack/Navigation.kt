package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.router.stack.navigate
import com.vsevolodganin.clicktrack.drawer.DrawerNavigation

interface Navigation : ScreenStackNavigation, DrawerNavigation

fun Navigation(
    stackNavigation: ScreenStackNavigation,
    drawerNavigation: DrawerNavigation
): Navigation {
    return NavigationImpl(stackNavigation, drawerNavigation)
}

fun Navigation.resetTo(config: ScreenConfiguration) {
    closeDrawer()
    navigate { listOf(it.first(), config) }
}

private class NavigationImpl(
    stackNavigation: ScreenStackNavigation,
    drawerNavigation: DrawerNavigation
) : Navigation, ScreenStackNavigation by stackNavigation, DrawerNavigation by drawerNavigation
