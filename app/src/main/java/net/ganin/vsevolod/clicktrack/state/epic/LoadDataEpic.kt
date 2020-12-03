package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.redux.Store
import net.ganin.vsevolod.clicktrack.state.AppState
import net.ganin.vsevolod.clicktrack.state.Screen
import net.ganin.vsevolod.clicktrack.state.actions.UpdateClickTrack
import net.ganin.vsevolod.clicktrack.state.actions.UpdateClickTrackList
import net.ganin.vsevolod.clicktrack.state.frontScreen
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository
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
