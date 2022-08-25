package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.drawer.DrawerViewModel

interface RootViewModel {
    val drawer: DrawerViewModel
    val screens: ScreenStackState
}
