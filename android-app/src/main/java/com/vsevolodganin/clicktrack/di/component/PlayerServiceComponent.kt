package com.vsevolodganin.clicktrack.di.component

import androidx.core.app.NotificationManagerCompat
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import com.vsevolodganin.clicktrack.IntentFactory
import com.vsevolodganin.clicktrack.notification.NotificationChannels
import com.vsevolodganin.clicktrack.player.AudioFocusManager
import com.vsevolodganin.clicktrack.player.PlayableContentProvider
import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.player.PlayerCore
import com.vsevolodganin.clicktrack.player.PlayerService
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class PlayerServiceScope

@PlayerServiceScope
@Component
abstract class PlayerServiceComponent(
    @Component protected val applicationComponent: ApplicationComponent,
    @get:Provides protected val playerService: PlayerService,
) {
    abstract val player: Player
    abstract val playableContentProvider: PlayableContentProvider
    abstract val userPreferences: UserPreferencesRepository
    abstract val intentFactory: IntentFactory
    abstract val notificationManager: NotificationManagerCompat
    abstract val notificationChannels: NotificationChannels
    abstract val audioFocusManager: AudioFocusManager

    protected val PlayerCore.player: Player @Provides get() = this
    protected val PlayerService.essentyLifecycle: Lifecycle @Provides get() = essentyLifecycle()
}
