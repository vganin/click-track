package com.vsevolodganin.clicktrack.player

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FLAG_FOREGROUND_SERVICE
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.res.ResourcesCompat
import com.vsevolodganin.clicktrack.IntentFactory
import com.vsevolodganin.clicktrack.MainApplication
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.notification.NotificationChannels
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.cast
import kotlinx.coroutines.CoroutineScope
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
import timber.log.Timber
import javax.inject.Inject

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
            context.startService(serviceIntent(context).apply {
                action = ACTION_PAUSE
            })
        }

        fun resume(context: Context) {
            context.startService(serviceIntent(context).apply {
                action = ACTION_RESUME
            })
        }

        fun stop(context: Context) {
            context.startService(serviceIntent(context).apply {
                action = ACTION_STOP
            })
        }

        fun bind(context: Context, serviceConnection: ServiceConnection) {
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
    }

    @Parcelize
    private data class State(
        val id: PlayableId,
        val startAtProgress: Double?,
        val soundsId: ClickSoundsId?,
        val isPaused: Boolean,
    ) : Parcelable

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var player: Player

    @Inject
    lateinit var playableContentProvider: PlayableContentProvider

    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    @Inject
    lateinit var intentFactory: IntentFactory

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var notificationChannels: NotificationChannels

    @Inject
    lateinit var audioFocusManager: AudioFocusManager

    @Inject
    lateinit var latencyTracker: LatencyTracker

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
        inject()

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

        latencyTracker.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        disposePlayer()
        latencyTracker.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> state.value = intent.getParcelableExtraCompat(EXTRA_START_ARGUMENTS)
                ?: throw RuntimeException("No start arguments were supplied")
            ACTION_STOP -> state.value = null
            ACTION_PAUSE -> state.update { it?.copy(isPaused = true) }
            ACTION_RESUME -> state.update { it?.copy(isPaused = false) }
            else -> {
                Timber.w("Undefined intent received = $intent, flags = $flags, startId = $startId")
                state.value = null
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = PlayerServiceBinder(player.playbackState())

    private fun initializePlayer() {
        scope.apply {
            launch { audioFocusComponent() }
            launch { foregroundComponent() }
            launch { playbackComponent() }
        }
    }

    private fun disposePlayer() = scope.cancel()

    private suspend fun audioFocusComponent() {
        combine(
            userPreferences.ignoreAudioFocus.flow,
            audioFocusManager.hasFocus()
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
                        when (val tapIntent = intentFactory.navigate(id)) {
                            null -> stopForeground()
                            else -> {
                                playableContentProvider.clickTrackFlow(id)
                                    .filterNotNull()
                                    .map { it.name }
                                    .distinctUntilChanged()
                                    .collectLatest { name ->
                                        startForeground(
                                            contentText = name,
                                            tapIntent = tapIntent,
                                            isPaused = args.isPaused
                                        )
                                    }
                            }
                        }
                    }
                    TwoLayerPolyrhythmId -> {
                        playableContentProvider.twoLayerPolyrhythmFlow()
                            .collectLatest { polyrhythm ->
                                startForeground(
                                    contentText = getString(
                                        R.string.player_service_notification_polyrhythm_title,
                                        polyrhythm.layer1,
                                        polyrhythm.layer2
                                    ),
                                    tapIntent = intentFactory.navigatePolyrhythms(),
                                    isPaused = args.isPaused
                                )
                            }
                    }
                }
            } else {
                stopForeground()
            }
        }
    }

    private suspend fun playbackComponent() = coroutineScope {
        launch {
            state.map { it?.isPaused }.distinctUntilChanged().collectLatest { isPaused ->
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
                        player.pause()
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
                        player.resume()
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

        launch {
            fun State.toPlayerInput() = Player.Input(id, startAtProgress, soundsId)

            state.map { it != null }.distinctUntilChanged().collectLatest { startPlay ->
                if (startPlay && requestAudioFocus()) {
                    player.play(
                        state
                            .filterNotNull()
                            .map(State::toPlayerInput)
                            .distinctUntilChanged()
                    )
                }

                audioFocusManager.releaseAudioFocus()
                state.emit(null)
            }
        }
    }

    private fun startForeground(contentText: String, tapIntent: Intent, isPaused: Boolean) {
        val launchAppIntent = PendingIntent.getActivity(this, 0, tapIntent, pendingIntentFlags)

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannels.playingNow)
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
            notificationManager.notify(R.id.notification_playing_now, notification)
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
        return userPreferences.ignoreAudioFocus.value || audioFocusManager.requestAudioFocus()
    }

    private fun mediaSessionPlaybackStateBuilder(): PlaybackStateCompat.Builder {
        return PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_STOP)
    }

    private fun inject() {
        application.cast<MainApplication>().daggerComponent.playerServiceComponentBuilder()
            .build()
            .inject(this)
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