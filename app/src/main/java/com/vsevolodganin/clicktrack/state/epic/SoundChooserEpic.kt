package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.sounds.SafAudioChooser
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.state.actions.SoundLibraryAction
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull

@ActivityScoped
class SoundChooserEpic @Inject constructor(
    private val soundChooser: SafAudioChooser,
    private val clickSoundsRepository: ClickSoundsRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions.filterIsInstance<SoundLibraryAction.ChooseClickSound>()
            .mapNotNull { action ->
                val initialUri = getInitialUri(action.id, action.type)
                val uri = soundChooser.chooseAudio(initialUri) ?: return@mapNotNull null
                SoundLibraryAction.UpdateClickSound(
                    id = action.id,
                    type = action.type,
                    source = ClickSoundSource.Uri(uri.toString()),
                    shouldStore = true,
                )
            }
    }

    private suspend fun getInitialUri(id: ClickSoundsId, type: ClickSoundType): String? {
        return when (id) {
            is ClickSoundsId.Builtin -> null
            is ClickSoundsId.Database -> when (val source = clickSoundsRepository.getById(id).firstOrNull()?.value?.beatByType(type)) {
                is ClickSoundSource.Bundled -> null
                is ClickSoundSource.Uri -> source.value
                null -> null
            }
        }
    }
}
