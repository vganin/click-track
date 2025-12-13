package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.player.PlaybackState
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import dev.zacsweers.metro.Inject

@MainControllerScope
@Inject
class DummyPlayerServiceAccessImpl : PlayerServiceAccess {
    override fun start(id: PlayableId, atProgress: Double?, soundsId: ClickSoundsId?) = Unit

    override fun pause() = Unit

    override fun resume() = Unit

    override fun stop() = Unit

    override fun playbackState(): Flow<PlaybackState?> = flowOf(null)
}
