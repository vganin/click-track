package com.vsevolodganin.clicktrack.about

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.common.BuildConfig
import com.vsevolodganin.clicktrack.common.LinkOpener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class AboutViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
    private val linkOpener: LinkOpener,
    buildConfig: BuildConfig
) : AboutViewModel, ComponentContext by componentContext {

    override val state: StateFlow<AboutState> = MutableStateFlow(
        AboutState(displayVersion = buildConfig.versionName)
    )

    override fun onBackClick() = navigation.pop()

    override fun onHomeClick() = linkOpener.url(HOME_PAGE)

    override fun onTwitterClick() = linkOpener.url(TWITTER)

    override fun onEmailClick() = linkOpener.email(EMAIL)

    override fun onArtstationClick() = linkOpener.url(ARTSTATION)

    override fun onProjectLinkClick() = linkOpener.url(GITHUB_PROJECT)

    private companion object Const {
        const val EMAIL = "contact@vsevolodganin.com"
        const val HOME_PAGE = "https://vsevolodganin.com"
        const val TWITTER = "https://twitter.com/vsga_dev"
        const val ARTSTATION = "https://varvara_furu.artstation.com"
        const val GITHUB_PROJECT = "https://github.com/vganin/click-track"
    }
}
