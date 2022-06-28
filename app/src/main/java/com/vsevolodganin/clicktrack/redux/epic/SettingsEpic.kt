package com.vsevolodganin.clicktrack.redux.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.redux.action.SettingsAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.theme.ThemeManager
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class SettingsEpic @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val themeManager: ThemeManager,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<SettingsAction.ChangeTheme>()
                .consumeEach { action ->
                    userPreferencesRepository.theme.edit {
                        action.value
                    }
                    withContext(mainDispatcher) {
                        themeManager.setTheme(action.value)
                    }
                },

            actions.filterIsInstance<SettingsAction.ChangeIgnoreAudioFocus>()
                .consumeEach { action ->
                    userPreferencesRepository.ignoreAudioFocus.edit {
                        action.value
                    }
                }
        )
    }
}
