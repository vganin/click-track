package com.vsevolodganin.clicktrack.di.component

import androidx.core.app.NotificationManagerCompat
import com.vsevolodganin.clicktrack.IntentFactory
import com.vsevolodganin.clicktrack.di.module.PlayerServiceModule
import com.vsevolodganin.clicktrack.notification.NotificationChannels
import com.vsevolodganin.clicktrack.player.AudioFocusManager
import com.vsevolodganin.clicktrack.player.LatencyTracker
import com.vsevolodganin.clicktrack.player.PlayableContentProvider
import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.log.Logger
import kotlinx.coroutines.CoroutineScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Scope

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class PlayerServiceScope

@PlayerServiceScope
@DependencyGraph
abstract class PlayerServiceComponent(
    @Includes protected val applicationComponent: ApplicationComponent,
) : PlayerServiceModule {
    abstract val scope: CoroutineScope
    abstract val player: Player
    abstract val playableContentProvider: PlayableContentProvider
    abstract val userPreferences: UserPreferencesRepository
    abstract val intentFactory: IntentFactory
    abstract val notificationManager: NotificationManagerCompat
    abstract val notificationChannels: NotificationChannels
    abstract val audioFocusManager: AudioFocusManager
    abstract val latencyTracker: LatencyTracker
    abstract val logger: Logger

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(applicationComponent: ApplicationComponent): PlayerServiceComponent
    }
}
