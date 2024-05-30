package com.vsevolodganin.clicktrack.player

import android.media.AudioManager
import android.media.AudioManager.AUDIOFOCUS_GAIN
import android.media.AudioManager.AUDIOFOCUS_LOSS
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

@PlayerServiceScope
@Inject
class AudioFocusManager(
    private val audioManager: AudioManager,
) {
    private val hasFocus = MutableStateFlow(false)

    private val focusRequest = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
        .setAudioAttributes(
            AudioAttributesCompat.Builder()
                .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                .setContentType(AudioAttributesCompat.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .setWillPauseWhenDucked(false)
        .setOnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AUDIOFOCUS_GAIN -> {
                    hasFocus.tryEmit(true)
                }
                AUDIOFOCUS_LOSS -> {
                    hasFocus.tryEmit(false)
                }
            }
        }
        .build()

    fun hasFocus(): StateFlow<Boolean> = hasFocus

    fun requestAudioFocus(): Boolean {
        return when (AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> true
            else -> false
        }.also {
            hasFocus.value = it
        }
    }

    fun releaseAudioFocus() {
        AudioManagerCompat.abandonAudioFocusRequest(audioManager, focusRequest)
        hasFocus.value = false
    }
}
