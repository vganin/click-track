package com.vsevolodganin.clicktrack.player

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FLAG_FOREGROUND_SERVICE
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.ServiceCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleService
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.applicationComponent
import com.vsevolodganin.clicktrack.di.component.PlayerServiceComponent
import com.vsevolodganin.clicktrack.di.component.create
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerService : LifecycleService() {

    companion object {
        fun bind(context: Context, serviceConnection: ServiceConnection) {
            if (!context.bindService(serviceIntent(context), serviceConnection, BIND_AUTO_CREATE)) {
                throw RuntimeException("Wasn't able to connect to player service")
            }
        }

        private fun serviceIntent(context: Context): Intent = Intent(context, PlayerService::class.java)

        private const val ACTION_PLAY = "play"
        private const val ACTION_STOP = "stop"
        private const val ACTION_PAUSE = "pause"
    }

    private lateinit var component: PlayerServiceComponent

    private lateinit var mediaSession: MediaSessionCompat

    private var isNotificationDisplayed = false

    private val pendingIntentFlags by lazy(LazyThreadSafetyMode.NONE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
    private val stopIntent by lazy(LazyThreadSafetyMode.NONE) {
        PendingIntent.getService(this, 0, serviceIntent(this).apply { action = ACTION_STOP }, pendingIntentFlags)
    }
    private val pauseIntent by lazy(LazyThreadSafetyMode.NONE) {
        PendingIntent.getService(this, 0, serviceIntent(this).apply { action = ACTION_PAUSE }, pendingIntentFlags)
    }
    private val resumeIntent by lazy(LazyThreadSafetyMode.NONE) {
        PendingIntent.getService(this, 0, serviceIntent(this).apply { action = ACTION_PLAY }, pendingIntentFlags)
    }

    private val scope = MainScope()

    override fun onCreate() {
        super.onCreate()

        component = PlayerServiceComponent::class.create(applicationComponent, this)

        mediaSession = MediaSessionCompat(this@PlayerService, "ClickTrackMediaSession").apply {
            setPlaybackState(mediaSessionPlaybackStateBuilder().build())
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    component.player.play()
                }

                override fun onPause() {
                    component.player.pause()
                }

                override fun onStop() {
                    component.player.stop()
                }
            })
        }

        scope.apply {
            launch { audioFocusComponent() }
            launch { foregroundComponent() }
            launch { mediaSessionComponent() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        scope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_PLAY -> component.player.play()
            ACTION_STOP -> component.player.stop()
            ACTION_PAUSE -> component.player.pause()
            else -> {
                Timber.e("Undefined intent received = $intent, flags = $flags, startId = $startId")
                component.player.stop()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)

        return PlayerServiceBinder(component.player)
    }

    private suspend fun audioFocusComponent() {
        combine(
            component.userPreferences.ignoreAudioFocus.flow,
            component.audioFocusManager.hasFocus()
        ) { ignoreAudioFocus, hasFocus -> ignoreAudioFocus || hasFocus }
            .collect { isAllowedToPlay ->
                if (!isAllowedToPlay) {
                    component.player.stop()
                }
            }
    }

    private suspend fun foregroundComponent() {
        component.player.playbackState.map { it?.id }.distinctUntilChanged().collectLatest { id ->
            when (id) {
                is ClickTrackId -> {
                    val tapIntent = component.intentFactory.navigate(id) ?: return@collectLatest stopForeground()
                    combine(
                        component.playableContentProvider.clickTrackFlow(id),
                        component.player.playbackState.map { it?.isPaused }.distinctUntilChanged()
                    ) { clickTrack, isPaused ->
                        clickTrack ?: return@combine stopForeground()
                        isPaused ?: return@combine stopForeground()
                        startForeground(
                            contentText = clickTrack.name,
                            tapIntent = tapIntent,
                            isPaused = isPaused
                        )
                    }.collect()
                }
                TwoLayerPolyrhythmId -> {
                    combine(
                        component.playableContentProvider.twoLayerPolyrhythmFlow(),
                        component.player.playbackState.map { it?.isPaused }.distinctUntilChanged()
                    ) { polyrhythm, isPaused ->
                        isPaused ?: return@combine stopForeground()
                        startForeground(
                            contentText = getString(
                                R.string.player_service_notification_polyrhythm_title,
                                polyrhythm.layer1,
                                polyrhythm.layer2
                            ),
                            tapIntent = component.intentFactory.navigatePolyrhythms(),
                            isPaused = isPaused
                        )
                    }.collect()
                }
                null -> stopForeground()
            }
        }
    }

    private suspend fun mediaSessionComponent() {
        component.player.playbackState.map { it?.isPaused }.distinctUntilChanged().collect { isPaused ->
            when (isPaused) {
                true -> {
                    mediaSession.apply {
                        isActive = true
                        setPlaybackState(
                            mediaSessionPlaybackStateBuilder()
                                .setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                                .build()
                        )
                    }
                }
                false -> {
                    mediaSession.apply {
                        isActive = true
                        setPlaybackState(
                            mediaSessionPlaybackStateBuilder()
                                .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                                .build()
                        )
                    }
                }
                null -> {
                    mediaSession.apply {
                        isActive = false
                        setPlaybackState(
                            mediaSessionPlaybackStateBuilder()
                                .setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                                .build()
                        )
                    }
                }
            }
        }
    }

    private fun startForeground(contentText: String, tapIntent: Intent, isPaused: Boolean) {
        val launchAppIntent = PendingIntent.getActivity(this, 0, tapIntent, pendingIntentFlags)

        val notificationBuilder = NotificationCompat.Builder(this, component.notificationChannels.playingNow)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ResourcesCompat.getColor(resources, R.color.debug_signature, null))
            .setColorized(true)
            .setContentTitle(getString(R.string.player_service_notification_playing_now))
            .setContentText(contentText)
            .setContentIntent(launchAppIntent)
            .setDeleteIntent(stopIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(VISIBILITY_PUBLIC)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setOngoing(true)
            .addAction(0, getString(R.string.player_service_notification_stop), stopIntent)
            .run {
                if (isPaused) {
                    addAction(0, getString(R.string.player_service_notification_resume), resumeIntent)
                } else {
                    addAction(0, getString(R.string.player_service_notification_pause), pauseIntent)
                }
            }

        if (isNotificationDisplayed) {
            val notification = notificationBuilder
                .build()
                .apply { flags = flags or FLAG_FOREGROUND_SERVICE }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                component.notificationManager.notify(R.id.notification_playing_now, notification)
            }
        } else {
            val notification = notificationBuilder.build()
            startForeground(R.id.notification_playing_now, notification)
            isNotificationDisplayed = true
        }
    }

    private fun stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        isNotificationDisplayed = false
    }

    private fun requestAudioFocus(): Boolean {
        return component.userPreferences.ignoreAudioFocus.value || component.audioFocusManager.requestAudioFocus()
    }

    private fun mediaSessionPlaybackStateBuilder(): PlaybackStateCompat.Builder {
        return PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_STOP)
    }

    private inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(name: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(name, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(name)
        }
    }
}
