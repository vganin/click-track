package com.vsevolodganin.clicktrack.list

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.common.NewClickTrackNameSuggester
import com.vsevolodganin.clicktrack.drawer.DrawerNavigation
import com.vsevolodganin.clicktrack.generated.resources.MR
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.DefaultCue
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.utils.collection.immutable.move
import com.vsevolodganin.clicktrack.utils.decompose.consumeSavedState
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import com.vsevolodganin.clicktrack.utils.decompose.pushIfUnique
import com.vsevolodganin.clicktrack.utils.decompose.registerSaveStateFor
import com.vsevolodganin.clicktrack.utils.resources.StringResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class ClickTrackListViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val stringResolver: StringResolver,
    private val navigation: ScreenStackNavigation,
    private val drawerNavigation: DrawerNavigation,
    private val clickTrackRepository: ClickTrackRepository,
    private val newClickTrackNameSuggester: NewClickTrackNameSuggester,
) : ClickTrackListViewModel, ComponentContext by componentContext {
    private val scope = coroutineScope()
    private val _state: MutableStateFlow<ClickTrackListState> = MutableStateFlow(consumeSavedState() ?: ClickTrackListState(emptyList()))

    override val state: StateFlow<ClickTrackListState> = _state

    init {
        registerSaveStateFor(state)

        scope.launch {
            clickTrackRepository.getAll()
                .map(::ClickTrackListState)
                .collect { _state.value = it }
        }
    }

    override fun onAddClick() {
        scope.launch {
            val suggestedNewClickTrackName = newClickTrackNameSuggester.suggest(
                withContext(Dispatchers.Main) {
                    stringResolver.resolve(MR.strings.general_unnamed_click_track_template)
                },
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

    override fun onMenuClick() = drawerNavigation.openDrawer()

    override fun onItemMove(from: Int, to: Int) {
        _state.update {
            it.copy(items = it.items.move(from, to))
        }
    }

    override fun onItemMoveFinished() {
        clickTrackRepository.updateOrdering(_state.value.items.map(ClickTrackWithDatabaseId::id))
    }

    private fun defaultNewClickTrack(suggestedNewClickTrackName: String) = ClickTrack(
        name = suggestedNewClickTrackName,
        cues = listOf(DefaultCue),
        loop = true,
    )
}
