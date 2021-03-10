package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.SettingsScreenState
import com.vsevolodganin.clicktrack.state.actions.SettingsAction
import com.vsevolodganin.clicktrack.state.frontScreen
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.ThemeManager
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withContext

@ViewModelScoped
class SettingsEpic @Inject constructor(
    private val store: Store<AppState>,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val themeManager: ThemeManager,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            store.state
                .map { it.backstack.frontScreen() }
                .distinctUntilChangedBy { it?.javaClass }
                .filterIsInstance<Screen.Settings>()
                .map {
                    SettingsScreenState(
                        theme = userPreferencesRepository.theme
                    )
                }
                .map(SettingsAction::SetScreenState),

            actions.filterIsInstance<SettingsAction.ChangeTheme>()
                .consumeEach { action ->
                    userPreferencesRepository.theme = action.value
                    withContext(mainDispatcher) {
                        themeManager.setTheme(action.value)
                    }
                }
        )
    }
}
