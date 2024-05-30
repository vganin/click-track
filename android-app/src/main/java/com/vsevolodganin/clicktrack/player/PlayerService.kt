package com.vsevolodganin.clicktrack.player

import android.Manifest
import android.app.PendingIntent
import android.app.Service
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
import androidx.core.content.IntentCompat
import androidx.core.content.res.ResourcesCompat
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.applicationComponent
import com.vsevolodganin.clicktrack.di.component.PlayerServiceComponent
import com.vsevolodganin.clicktrack.di.component.create
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import com.vsevolodganin.clicktrack.multiplatform.R as MR

class PlayerService : Service() {
    companion object {
        fun start(
            context: Context,
            id: PlayableId,
            atProgress: Double?,
            soundsId: ClickSoundsId?,
        ) {
            val arguments = State(id, atProgress, soundsId, isPaused = false)
            val intent = serviceIntent(context).apply {
                action = ACTION_START
                putExtra(EXTRA_START_ARGUMENTS, arguments)
            }
            context.startService(intent)
        }

        fun pause(context: Context) {
            context.startService(
                serviceIntent(context).apply {
                    action = ACTION_PAUSE
                },
            )
        }

        fun resume(context: Context) {
            context.startService(
                serviceIntent(context).apply {
                    action = ACTION_RESUME
                },
            )
        }

        fun stop(context: Context) {
            context.startService(
                serviceIntent(context).apply {
                    action = ACTION_STOP
                },
            )
        }

        fun bind(
            context: Context,
            serviceConnection: ServiceConnection,
        ) {
            if (!context.bindService(serviceIntent(context), serviceConnection, BIND_AUTO_CREATE)) {
                throw RuntimeException("Wasn't able to connect to player service")
            }
        }

        private fun serviceIntent(context: Context): Intent = Intent(context, PlayerService::class.java)

        private const val EXTRA_START_ARGUMENTS = "start_arguments"
        private const val ACTION_START = "start"
        private const val ACTION_STOP = "stop"
        private const val ACTION_PAUSE = "pause"
        private const val ACTION_RESUME = "resume"

        private const val TAG = "PlayerService"
    }

    @Parcelize
    private data class State(
        val id: PlayableId,
        val startAtProgress: Double?,
        val soundsId: ClickSoundsId?,
        val isPaused: Boolean,
    ) : Parcelable

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
        PendingIntent.getService(this, 0, serviceIntent(this).apply { action = ACTION_RESUME }, pendingIntentFlags)
    }

    private val state = MutableStateFlow<State?>(null)

    override fun onCreate() {
        super.onCreate()

        component = PlayerServiceComponent::class.create(applicationComponent)

        mediaSession = MediaSessionCompat(this@PlayerService, "ClickTrackMediaSession").apply {
            setPlaybackState(mediaSessionPlaybackStateBuilder().build())
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    state.update { it?.copy(isPaused = false) }
                }

                override fun onPause() {
                    state.update { it?.copy(isPaused = true) }
                }

                override fun onStop() {
                    state.value = null
                }
            })
        }

        initializePlayer()

        component.latencyTracker.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        disposePlayer()
        component.latencyTracker.stop()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            ACTION_START -> state.value = IntentCompat.getParcelableExtra(intent, EXTRA_START_ARGUMENTS, State::class.java)
                ?: throw RuntimeException("No start arguments were supplied")

            ACTION_STOP -> state.value = null
            ACTION_PAUSE -> state.update { it?.copy(isPaused = true) }
            ACTION_RESUME -> state.update { it?.copy(isPaused = false) }
            else -> {
                component.logger.logError(TAG, "Undefined intent received = $intent, flags = $flags, startId = $startId")
                state.value = null
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = PlayerServiceBinder(component.player.playbackState())

    private fun initializePlayer() {
        component.scope.apply {
            launch { audioFocusComponent() }
            launch { foregroundComponent() }
            launch { playbackComponent() }
        }
    }

    private fun disposePlayer() = component.scope.cancel()

    private suspend fun audioFocusComponent() {
        combine(
            component.userPreferences.ignoreAudioFocus.flow,
            component.audioFocusManager.hasFocus(),
        ) { ignoreAudioFocus, hasFocus -> ignoreAudioFocus || hasFocus }
            .collect { isAllowedToPlay ->
                if (!isAllowedToPlay) {
                    state.emit(null)
                }
            }
    }

    private suspend fun foregroundComponent() {
        state.collectLatest { args ->
            if (args != null) {
                when (val id = args.id) {
                    is ClickTrackId -> {
                        when (val tapIntent = component.intentFactory.navigate(id)) {
                            null -> stopForeground()
                            else -> {
                                component.playableContentProvider.clickTrackFlow(id)
                                    .filterNotNull()
                                    .map { it.name }
                                    .distinctUntilChanged()
                                    .collectLatest { name ->
                                        startForeground(
                                            contentText = name,
                                            tapIntent = tapIntent,
                                            isPaused = args.isPaused,
                                        )
                                    }
                            }
                        }
                    }

                    TwoLayerPolyrhythmId -> {
                        component.playableContentProvider.twoLayerPolyrhythmFlow()
                            .collectLatest { polyrhythm ->
                                startForeground(
                                    contentText = getString(
                                        MR.string.player_service_notification_polyrhythm_title,
                                        polyrhythm.layer1,
                                        polyrhythm.layer2,
                                    ),
                                    tapIntent = component.intentFactory.navigatePolyrhythms(),
                                    isPaused = args.isPaused,
                                )
                            }
                    }
                }
            } else {
                stopForeground()
            }
        }
    }

    private suspend fun playbackComponent() =
        coroutineScope {
            launch {
                state.map { it?.isPaused }.distinctUntilChanged().collectLatest { isPaused ->
                    when (isPaused) {
                        true -> {
                            mediaSession.apply {
                                isActive = true
                                setPlaybackState(
                                    mediaSessionPlaybackStateBuilder()
                                        .setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                                        .build(),
                                )
                            }
                            component.player.pause()
                        }

                        false -> {
                            mediaSession.apply {
                                isActive = true
                                setPlaybackState(
                                    mediaSessionPlaybackStateBuilder()
                                        .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                                        .build(),
                                )
                            }
                            component.player.resume()
                        }

                        null -> {
                            mediaSession.apply {
                                isActive = false
                                setPlaybackState(
                                    mediaSessionPlaybackStateBuilder()
                                        .setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                                        .build(),
                                )
                            }
                        }
                    }
                }
            }

            launch {
                fun State.toPlayerInput() = Player.Input(id, startAtProgress, soundsId)

                state.map { it != null }.distinctUntilChanged().collectLatest { startPlay ->
                    if (startPlay && requestAudioFocus()) {
                        component.player.play(
                            state
                                .filterNotNull()
                                .map(State::toPlayerInput)
                                .distinctUntilChanged(),
                        )
                    }

                    component.audioFocusManager.releaseAudioFocus()
                    state.emit(null)
                }
            }
        }

    private fun startForeground(
        contentText: String,
        tapIntent: Intent,
        isPaused: Boolean,
    ) {
        val launchAppIntent = PendingIntent.getActivity(this, 0, tapIntent, pendingIntentFlags)

        val notificationBuilder = NotificationCompat.Builder(this, component.notificationChannels.playingNow)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ResourcesCompat.getColor(resources, MR.color.debug_signature, null))
            .setColorized(true)
            .setContentTitle(getString(MR.string.player_service_notification_playing_now))
            .setContentText(contentText)
            .setContentIntent(launchAppIntent)
            .setDeleteIntent(stopIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(VISIBILITY_PUBLIC)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setOngoing(true)
            .addAction(0, getString(MR.string.player_service_notification_stop), stopIntent)
            .run {
                if (isPaused) {
                    addAction(0, getString(MR.string.player_service_notification_resume), resumeIntent)
                } else {
                    addAction(0, getString(MR.string.player_service_notification_pause), pauseIntent)
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
}
