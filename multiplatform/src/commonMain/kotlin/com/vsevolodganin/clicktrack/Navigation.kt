package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.drawer.DrawerNavigation

interface Navigation : ScreenStackNavigation, DrawerNavigation

fun Navigation(
    stackNavigation: ScreenStackNavigation,
    drawerNavigation: DrawerNavigation
): Navigation {
    return NavigationImpl(stackNavigation, drawerNavigation)
}

private class NavigationImpl(
    stackNavigation: ScreenStackNavigation,
    drawerNavigation: DrawerNavigation
) : Navigation, ScreenStackNavigation by stackNavigation, DrawerNavigation by drawerNavigation
