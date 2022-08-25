package com.vsevolodganin.clicktrack.list

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.push
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.common.NewClickTrackNameSuggester
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.DefaultCue
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ClickTrackListViewModelImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
    private val clickTrackRepository: ClickTrackRepository,
    private val newClickTrackNameSuggester: NewClickTrackNameSuggester,
) : ClickTrackListViewModel, ComponentContext by componentContext {

    override val state: StateFlow<ClickTrackListState> =
        clickTrackRepository.getAll()
            .map(::ClickTrackListState)
            .stateIn(MainScope(), SharingStarted.Eagerly, ClickTrackListState(emptyList()))

    override fun onAddClick() {
        val suggestedNewClickTrackName = newClickTrackNameSuggester.suggest()
        val newClickTrack = defaultNewClickTrack(suggestedNewClickTrackName)
        val newClickTrackId = clickTrackRepository.insert(newClickTrack)
        navigation.push(ScreenConfiguration.EditClickTrack(id = newClickTrackId, isInitialEdit = true))
    }

    override fun onItemClick(id: ClickTrackId.Database) = navigation.push(ScreenConfiguration.PlayClickTrack(id = id))

    override fun onItemRemove(id: ClickTrackId.Database) = clickTrackRepository.remove(id)

    override fun onMenuClick() = navigation.openDrawer()

    private fun defaultNewClickTrack(suggestedNewClickTrackName: String) = ClickTrack(
        name = suggestedNewClickTrackName,
        cues = listOf(DefaultCue),
        loop = true,
    )
}
