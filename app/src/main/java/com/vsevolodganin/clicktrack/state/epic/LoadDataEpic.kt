package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.UpdateClickTrack
import com.vsevolodganin.clicktrack.state.actions.UpdateClickTrackList
import com.vsevolodganin.clicktrack.state.frontScreen
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@ViewModelScoped
class LoadDataEpic @Inject constructor(
    private val store: Store<AppState>,
    private val clickTrackRepository: ClickTrackRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            store.state
                .map { it.backstack.frontScreen() }
                .distinctUntilChangedBy { it?.javaClass }
                .flatMapLatest { screen ->
                    if (screen is Screen.ClickTrackList) {
                        clickTrackRepository.getAll()
                    } else {
                        emptyFlow()
                    }
                }
                .map(::UpdateClickTrackList),

            store.state
                .map { it.backstack.frontScreen() }
                .distinctUntilChangedBy { it?.javaClass }
                .flatMapLatest { screen ->
                    if (screen is Screen.PlayClickTrack) {
                        clickTrackRepository.getById(screen.state.clickTrack.id).filterNotNull()
                    } else {
                        emptyFlow()
                    }
                }
                .map(::UpdateClickTrack),
        )
    }
}
