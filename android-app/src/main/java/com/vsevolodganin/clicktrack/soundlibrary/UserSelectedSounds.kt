package com.vsevolodganin.clicktrack.soundlibrary

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.model.ClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class UserSelectedSounds(
    userPreferencesRepository: UserPreferencesRepository,
    private val clickSoundsRepository: ClickSoundsRepository,
) {
    private val cached = userPreferencesRepository.selectedSoundsId.flow
        .flatMapLatest(::soundsById)
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    fun get(): StateFlow<ClickSounds?> = cached

    private fun soundsById(soundsId: ClickSoundsId): Flow<ClickSounds?> {
        return when (soundsId) {
            is ClickSoundsId.Builtin -> flowOf(soundsId.value.sounds)
            is ClickSoundsId.Database -> clickSoundsRepository.getById(soundsId).map { it?.value }
        }
    }
}
