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
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Scope

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class PlayerServiceScope

@PlayerServiceScope
@Component
abstract class PlayerServiceComponent(
    @Component protected val applicationComponent: ApplicationComponent
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
}
