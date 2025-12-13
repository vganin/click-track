package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.subscribe
import com.arkivanov.essenty.backhandler.BackCallback
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.drawer.DrawerNavigation
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.zacsweers.metro.Inject

@MainControllerScope
@Inject
class RootViewModelImpl(
    private val componentContext: ComponentContext,
    private val drawerViewModel: DrawerViewModel,
    private val drawerNavigation: DrawerNavigation,
    private val screenStackNavigation: ScreenStackNavigation,
    private val screenStackState: ScreenStackState,
) : RootViewModel, ComponentContext by componentContext {
    private val scope = coroutineScope()

    override val drawer get() = drawerViewModel
    override val screens get() = screenStackState

    init {
        // Order is important: latter takes higher precedence
        implementScreenStackBackCallback()
        implementDrawerBackCallback()
    }

    private fun implementScreenStackBackCallback() {
        val callback = BackCallback(isEnabled = screenStackState.value.backStack.isNotEmpty(), onBack = screenStackNavigation::pop)
        screenStackState.subscribe(lifecycle) { state ->
            callback.isEnabled = state.backStack.isNotEmpty()
        }
        backHandler.register(callback)
    }

    private fun implementDrawerBackCallback() {
        val callback = BackCallback(isEnabled = drawerViewModel.state.value.isOpened, onBack = drawerNavigation::closeDrawer)
        scope.launch(context = Dispatchers.Unconfined, start = CoroutineStart.UNDISPATCHED) {
            drawerViewModel.state.collect { state ->
                callback.isEnabled = state.isOpened
            }
        }
        backHandler.register(callback)
    }
}
