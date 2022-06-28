package com.vsevolodganin.clicktrack.redux.epic

import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.redux.action.SoundLibraryAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.sounds.SafAudioChooser
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@ActivityScoped
class SoundChooserEpic @Inject constructor(
    private val soundChooser: SafAudioChooser,
    private val clickSoundsRepository: ClickSoundsRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions.filterIsInstance<SoundLibraryAction.SelectClickSound>()
            .mapNotNull { action ->
                val initialUri = getInitialUri(action.id, action.type)
                val uri = soundChooser.chooseAudio(initialUri) ?: return@mapNotNull null

                SoundLibraryAction.UpdateClickSound(
                    id = action.id,
                    type = action.type,
                    source = ClickSoundSource.Uri(uri.toString()),
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
