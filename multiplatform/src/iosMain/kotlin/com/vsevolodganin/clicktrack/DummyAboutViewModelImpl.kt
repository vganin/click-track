package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.about.AboutState
import com.vsevolodganin.clicktrack.about.AboutViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DummyAboutViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
) : AboutViewModel, ComponentContext by componentContext {

    override val state: StateFlow<AboutState> = MutableStateFlow(
        AboutState(
            displayVersion = "6.6.6"
        )
    )

    override fun onBackClick() = navigation.pop()
    override fun onHomeClick() = Unit
    override fun onTwitterClick() = Unit
    override fun onEmailClick() = Unit
    override fun onArtstationClick() = Unit
    override fun onProjectLinkClick() = Unit
}
