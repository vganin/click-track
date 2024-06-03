package com.vsevolodganin.clicktrack.drawer

import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.router.children.NavigationSource
import com.arkivanov.decompose.router.children.SimpleNavigation

interface DrawerNavigation {
    fun openDrawer()

    fun closeDrawer()
}

interface DrawerNavigationSource : NavigationSource<Boolean>

class DrawerNavigationImpl : DrawerNavigation, DrawerNavigationSource {

    private val impl = SimpleNavigation<Boolean>()

    override fun openDrawer() = impl.navigate(true)

    override fun closeDrawer() = impl.navigate(false)

    override fun subscribe(observer: (Boolean) -> Unit): Cancellation = impl.subscribe(observer)
}
