package com.vsevolodganin.clicktrack.player

import kotlinx.cinterop.ExperimentalForeignApi
import platform.MediaPlayer.MPMediaItemPropertyArtist
import platform.MediaPlayer.MPMediaItemPropertyTitle
import platform.MediaPlayer.MPNowPlayingInfoCenter
import platform.MediaPlayer.MPNowPlayingInfoMediaTypeAudio
import platform.MediaPlayer.MPNowPlayingInfoPropertyIsLiveStream
import platform.MediaPlayer.MPNowPlayingInfoPropertyMediaType
import platform.MediaPlayer.MPNowPlayingPlaybackStatePaused
import platform.MediaPlayer.MPNowPlayingPlaybackStatePlaying
import platform.MediaPlayer.MPNowPlayingPlaybackStateStopped
import platform.MediaPlayer.MPRemoteCommandCenter
import platform.MediaPlayer.MPRemoteCommandHandlerStatusSuccess

@OptIn(ExperimentalForeignApi::class)
class NowPlayingInfoCenterNotification : AudioSessionNotification {

    private var onPlay: () -> Unit = {}
    private var onPause: () -> Unit = {}
    private var onStop: () -> Unit = {}

    private val nowPlayingInfoCenter = MPNowPlayingInfoCenter.defaultCenter()
    private val remoteCommandCenter = MPRemoteCommandCenter.sharedCommandCenter()

    override fun setCallbacks(onPause: () -> Unit, onResume: () -> Unit, onStop: () -> Unit) {
        this.onPlay = onResume
        this.onPause = onPause
        this.onStop = onStop
    }

    override fun show(title: String, contentText: String, isPaused: Boolean) {
        with(nowPlayingInfoCenter) {
            nowPlayingInfo = mapOf(
                MPMediaItemPropertyTitle to title,
                MPMediaItemPropertyArtist to contentText,
                MPNowPlayingInfoPropertyIsLiveStream to true,
                MPNowPlayingInfoPropertyMediaType to MPNowPlayingInfoMediaTypeAudio,
            )
            playbackState = if (isPaused) MPNowPlayingPlaybackStatePaused else MPNowPlayingPlaybackStatePlaying
        }

        with(remoteCommandCenter) {
            arrayOf(previousTrackCommand, nextTrackCommand, skipBackwardCommand, skipForwardCommand).forEach {
                it.setEnabled(false)
            }

            with(playCommand) {
                setEnabled(true)
                removeTarget(null)
                addTargetWithHandler {
                    onPlay()
                    MPRemoteCommandHandlerStatusSuccess
                }
            }
            with(pauseCommand) {
                setEnabled(true)
                removeTarget(null)
                addTargetWithHandler {
                    onPause()
                    MPRemoteCommandHandlerStatusSuccess
                }
            }
            with(stopCommand) {
                setEnabled(true)
                removeTarget(null)
                addTargetWithHandler {
                    onStop()
                    MPRemoteCommandHandlerStatusSuccess
                }
            }
        }
    }

    override fun hide() {
        with(nowPlayingInfoCenter) {
            nowPlayingInfo = null
            playbackState = MPNowPlayingPlaybackStateStopped
        }

        with(remoteCommandCenter) {
            arrayOf(playCommand, pauseCommand, stopCommand).forEach {
                with(it) {
                    setEnabled(false)
                    removeTarget(null)
                }
            }
        }
    }
}
