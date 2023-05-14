package com.vsevolodganin.clicktrack.list

import ClickTrack.multiplatform.MR
import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.common.NewClickTrackNameSuggester
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.DefaultCue
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.utils.decompose.consumeSavedState
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.decompose.pushIfUnique
import com.vsevolodganin.clicktrack.utils.decompose.registerSaveStateFor
import com.vsevolodganin.clicktrack.utils.resources.StringResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class ClickTrackListViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val stringResolver: StringResolver,
    private val navigation: Navigation,
    private val clickTrackRepository: ClickTrackRepository,
    private val newClickTrackNameSuggester: NewClickTrackNameSuggester,
) : ClickTrackListViewModel, ComponentContext by componentContext {

    private val scope = coroutineScope()

    override val state: StateFlow<ClickTrackListState> =
        clickTrackRepository.getAll()
            .map(::ClickTrackListState)
            .stateIn(scope, SharingStarted.Eagerly, consumeSavedState() ?: ClickTrackListState(emptyList()))

    init {
        registerSaveStateFor(state)
    }

    override fun onAddClick() {
        scope.launch {
            val suggestedNewClickTrackName = newClickTrackNameSuggester.suggest(
                withContext(Dispatchers.Main) {
                    stringResolver.resolve(MR.strings.general_unnamed_click_track_template)
                }
            )
            val newClickTrack = defaultNewClickTrack(suggestedNewClickTrackName)
            val newClickTrackId = clickTrackRepository.insert(newClickTrack)
            withContext(Dispatchers.Main) {
                navigation.pushIfUnique(ScreenConfiguration.EditClickTrack(id = newClickTrackId, isInitialEdit = true))
            }
        }
    }

    override fun onItemClick(id: ClickTrackId.Database) = navigation.pushIfUnique(ScreenConfiguration.PlayClickTrack(id = id))

    override fun onItemRemove(id: ClickTrackId.Database) {
        scope.launch {
            clickTrackRepository.remove(id)
        }
    }

    override fun onMenuClick() = navigation.openDrawer()

    private fun defaultNewClickTrack(suggestedNewClickTrackName: String) = ClickTrack(
        name = suggestedNewClickTrackName,
        cues = listOf(DefaultCue),
        loop = true,
    )
}
