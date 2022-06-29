package com.vsevolodganin.clicktrack.redux.epic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.redux.action.SoundLibraryAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundSource
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@ActivityScoped
class SoundChooserEpic @Inject constructor(
    private val activity: AppCompatActivity,
    private val clickSoundsRepository: ClickSoundsRepository,
    private val savedStateHandle: SavedStateHandle,
) : Epic {
    private val resultsChannel = Channel<Pair<OpenAudioRequest, Uri>>(UNLIMITED)
    private val launcher = activity.registerForActivityResult(OpenAudio()) { uri ->
        val request = OpenAudioRequest.from(savedStateHandle)
        if (uri != null && request != null) {
            obtainPersistentPermissions(uri)
            resultsChannel.trySend(request to uri)
        }
    }

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<SoundLibraryAction.SelectClickSound>()
                .consumeEach { action ->
                    val initialUri = getInitialUri(action.id, action.type)
                    OpenAudioRequest(action.id, action.type).put(savedStateHandle)
                    launcher.launch(initialUri)
                },
            resultsChannel.consumeAsFlow()
                .map { (request, uri) ->
                    SoundLibraryAction.UpdateClickSound(
                        id = request.id,
                        type = request.type,
                        source = ClickSoundSource.Uri(uri.toString()),
                    )
                }
        )
    }

    private suspend fun getInitialUri(id: ClickSoundsId, type: ClickSoundType): Uri? {
        return when (id) {
            is ClickSoundsId.Builtin -> null
            is ClickSoundsId.Database -> when (val source = clickSoundsRepository.getById(id).firstOrNull()?.value?.beatByType(type)) {
                is ClickSoundSource.Bundled -> null
                is ClickSoundSource.Uri -> source.value
                null -> null
            }
        }?.let(Uri::parse)
    }

    private fun obtainPersistentPermissions(uri: Uri) {
        val contentResolver = activity.contentResolver
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    @Parcelize
    private class OpenAudioRequest(
        val id: ClickSoundsId.Database,
        val type: ClickSoundType,
    ) : Parcelable {

        fun put(savedStateHandle: SavedStateHandle) {
            savedStateHandle[SAVED_STATE_KEY] = this
        }

        companion object {
            fun from(savedStateHandle: SavedStateHandle): OpenAudioRequest? {
                return savedStateHandle[SAVED_STATE_KEY]
            }

            private const val SAVED_STATE_KEY = "open_audio_request"
        }
    }

    private class OpenAudio : ActivityResultContract<Uri?, Uri?>() {

        override fun createIntent(context: Context, input: Uri?): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "audio/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, input)
                }
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.takeIf { resultCode == Activity.RESULT_OK }?.data
        }

        override fun getSynchronousResult(context: Context, input: Uri?): SynchronousResult<Uri?>? = null
    }
}
