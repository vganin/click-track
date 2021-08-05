package com.vsevolodganin.clicktrack.state.redux.epic

import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.state.logic.LinkOpener
import com.vsevolodganin.clicktrack.state.redux.action.AboutAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge

@ActivityScoped
class AboutEpic @Inject constructor(
    private val linkOpener: LinkOpener,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<AboutAction.SendEmail>()
                .consumeEach { linkOpener.email(Const.EMAIL) },

            actions.filterIsInstance<AboutAction.GoHomePage>()
                .consumeEach { linkOpener.url(Const.HOME_PAGE) },

            actions.filterIsInstance<AboutAction.GoTwitter>()
                .consumeEach { linkOpener.url(Const.TWITTER) },

            actions.filterIsInstance<AboutAction.GoArtstation>()
                .consumeEach { linkOpener.url(Const.ARTSTATION) },
        )
    }

    private object Const {
        const val EMAIL = "vsevolod.ganin@gmail.com"
        const val HOME_PAGE = "https://dev.vsevolodganin.com"
        const val TWITTER = "https://twitter.com/vsevolod_ganin"
        const val ARTSTATION = "https://cacao_warrior.artstation.com/"
    }
}
