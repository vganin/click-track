package com.vsevolodganin.clicktrack.about

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.BuildConfig
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.common.LinkOpener
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AboutViewModelImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
    private val linkOpener: LinkOpener,
) : AboutViewModel, ComponentContext by componentContext {

    override val state: StateFlow<AboutState> = MutableStateFlow(
        AboutState(displayVersion = BuildConfig.DISPLAY_VERSION)
    )

    override fun onBackClick() = navigation.pop()

    override fun onHomeClick() = linkOpener.url(HOME_PAGE)

    override fun onTwitterClick() = linkOpener.url(TWITTER)

    override fun onEmailClick() = linkOpener.email(EMAIL)

    override fun onArtstationClick() = linkOpener.url(ARTSTATION)

    private companion object Const {
        const val EMAIL = "contact@vsevolodganin.com"
        const val HOME_PAGE = "https://dev.vsevolodganin.com"
        const val TWITTER = "https://twitter.com/vsevolod_ganin"
        const val ARTSTATION = "https://cacao_warrior.artstation.com/"
    }
}
