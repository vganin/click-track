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
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FLAG_FOREGROUND_SERVICE
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.vsevolodganin.clicktrack.Application
import com.vsevolodganin.clicktrack.IntentFactory
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.lib.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.player.PlayerService.NotificationConst.PLAYING_NOW_CHANNEL_ID
import com.vsevolodganin.clicktrack.player.PlayerService.NotificationConst.PLAYING_NOW_NOTIFICATION_ID
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.utils.cast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import javax.inject.Inject

class PlayerService : Service() {

    companion object {
        fun start(
            context: Context,
            clickTrack: ClickTrackWithId,
            atProgress: Double?,
            soundsId: ClickSoundsId?,
            keepInBackground: Boolean,
        ) {
            val arguments = StartClickTrackArguments(clickTrack, atProgress, soundsId, keepInBackground)
            val intent = serviceIntent(context).apply {
                action = ACTION_START_CLICK_TRACK
                putExtra(EXTRA_START_CLICK_TRACK_ARGUMENTS, arguments)
            }
            context.startService(intent)
        }

        fun start(
            context: Context,
            twoLayerPolyrhythm: TwoLayerPolyrhythm,
            atProgress: Double?,
            soundsId: ClickSoundsId?,
        ) {
            val arguments = StartPolyrhythmArguments(twoLayerPolyrhythm, atProgress, soundsId)
            val intent = serviceIntent(context).apply {
                action = ACTION_START_POLYRHYTHM
                putExtra(EXTRA_START_POLYRHYTHM_ARGUMENTS, arguments)
            }
            context.startService(intent)
        }

        fun pause(context: Context) {
            context.startService(serviceIntent(context).apply {
                action = ACTION_PAUSE
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

        private const val EXTRA_START_CLICK_TRACK_ARGUMENTS = "start_click_track_arguments"
        private const val EXTRA_START_POLYRHYTHM_ARGUMENTS = "start_polyrhythm_arguments"
        private const val ACTION_START_CLICK_TRACK = "start_click_track"
        private const val ACTION_START_POLYRHYTHM = "start_polyrhythm"
        private const val ACTION_STOP = "stop"
        private const val ACTION_PAUSE = "pause"

        @Parcelize
        private class StartClickTrackArguments(
            val clickTrack: ClickTrackWithId,
            val startAtProgress: Double?,
            val soundsId: ClickSoundsId?,
            val keepInBackground: Boolean,
        ) : Parcelable

        @Parcelize
        private class StartPolyrhythmArguments(
            val twoLayerPolyrhythm: TwoLayerPolyrhythm,
            val startAtProgress: Double?,
            val soundsId: ClickSoundsId?,
        ) : Parcelable
    }

    @Inject
    lateinit var player: Player

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    lateinit var intentFactory: IntentFactory

    @Inject
    lateinit var audioFocusManager: AudioFocusManager

    private var isNotificationDisplayed = false

    private lateinit var mediaSessionPlaybackStateBuilder: PlaybackStateCompat.Builder
    private lateinit var mediaSessionCallback: MediaSessionCompat.Callback
    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()

        inject()

        launchImmediately {
            player.playbackState().drop(1).collect {
                if (it == null) {
                    stop(this@PlayerService)
                }
            }
        }

        launchImmediately {
            audioFocusManager.focusLossFlow().collect {
                stop(this@PlayerService)
            }
        }

        mediaSessionPlaybackStateBuilder = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_STOP)
        mediaSessionCallback = object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                Timber.d("onPlay")
                // TODO: Support resuming
            }

            override fun onPause() {
                Timber.d("onPause")
                // TODO: Support pausing properly first
                stop(this@PlayerService)
            }

            override fun onStop() {
                Timber.d("onStop")
                stop(this@PlayerService)
            }
        }
        mediaSession = MediaSessionCompat(this, "ClickTrackMediaSession").apply {
            setPlaybackState(mediaSessionPlaybackStateBuilder.build())
            setCallback(mediaSessionCallback)
            isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_START_CLICK_TRACK -> {
                val args: StartClickTrackArguments = intent.getParcelableExtra(EXTRA_START_CLICK_TRACK_ARGUMENTS)
                    ?: throw RuntimeException("No start arguments were supplied")

                val clickTrack = args.clickTrack

                startPlayer(clickTrack, args.startAtProgress, args.soundsId)

                if (args.keepInBackground) {
                    startForeground(
                        contentText = clickTrack.value.name,
                        tapIntent = intentFactory.openClickTrack(clickTrack)
                    )
                } else {
                    stopForeground()
                }

                START_STICKY
            }
            ACTION_START_POLYRHYTHM -> {
                val args: StartPolyrhythmArguments = intent.getParcelableExtra(EXTRA_START_POLYRHYTHM_ARGUMENTS)
                    ?: throw RuntimeException("No start arguments were supplied")

                val polyrhythm = args.twoLayerPolyrhythm

                startPlayer(polyrhythm, args.startAtProgress, args.soundsId)

                startForeground(
                    contentText = getString(R.string.polyrhythm_notification_title, polyrhythm.layer1, polyrhythm.layer2),
                    tapIntent = intentFactory.openPolyrhythms()
                )

                START_STICKY
            }
            ACTION_STOP -> {
                stopPlayer()
                stopForeground()
                stopSelf()

                START_NOT_STICKY
            }
            ACTION_PAUSE -> {
                pausePlayer()

                START_STICKY
            }
            else -> { // The service was recreated with no intent so just stop everything
                stopPlayer()
                stopForeground()
                stopSelf()

                START_NOT_STICKY
            }
        }
    }

    override fun onDestroy() {
        stopPlayer()
        mediaSession.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder = PlayerServiceBinder(player.playbackState())

    private fun startPlayer(clickTrack: ClickTrackWithId, atProgress: Double?, soundsId: ClickSoundsId?) = launchImmediately {
        if (!audioFocusManager.requestAudioFocus()) {
            return@launchImmediately
        }

        mediaSession.setPlaybackState(
            mediaSessionPlaybackStateBuilder
                .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                .build()
        )

        player.start(clickTrack, atProgress, soundsId)
    }

    private fun startPlayer(twoLayerPolyrhythm: TwoLayerPolyrhythm, atProgress: Double?, soundsId: ClickSoundsId?) = launchImmediately {
        if (!audioFocusManager.requestAudioFocus()) {
            return@launchImmediately
        }

        mediaSession.setPlaybackState(
            mediaSessionPlaybackStateBuilder
                .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                .build()
        )

        player.start(twoLayerPolyrhythm, atProgress, soundsId)
    }

    private fun pausePlayer() = launchImmediately {
        player.pause()

        mediaSession.setPlaybackState(
            mediaSessionPlaybackStateBuilder
                .setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
                .build()
        )
    }

    private fun stopPlayer() = launchImmediately {
        player.stop()

        audioFocusManager.releaseAudioFocus()

        mediaSession.setPlaybackState(
            mediaSessionPlaybackStateBuilder
                .setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
                .build()
        )
    }

    private fun startForeground(contentText: String, tapIntent: Intent) {
        val channel = NotificationChannelCompat.Builder(PLAYING_NOW_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_MIN)
            .setName(getString(R.string.notification_channel_playing_now))
            .build()
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(channel)

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val launchAppIntent = PendingIntent.getActivity(this, 0, tapIntent, pendingIntentFlags)
        val stopServiceIntent = PendingIntent.getService(this, 0, serviceIntent(this).apply { action = ACTION_STOP }, pendingIntentFlags)

        val notificationBuilder = NotificationCompat.Builder(this, PLAYING_NOW_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ResourcesCompat.getColor(resources, R.color.signature, null))
            .setColorized(true)
            .setContentTitle(getString(R.string.notification_playing_now))
            .setContentText(contentText)
            .setContentIntent(launchAppIntent)
            .setDeleteIntent(stopServiceIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(VISIBILITY_PUBLIC)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setOngoing(true)
            .addAction(0, getString(R.string.notification_stop), stopServiceIntent)

        if (isNotificationDisplayed) {
            val notification = notificationBuilder
                .build()
                .apply { flags = flags or FLAG_FOREGROUND_SERVICE }
            notificationManager.notify(PLAYING_NOW_NOTIFICATION_ID, notification)
        } else {
            val notification = notificationBuilder.build()
            startForeground(PLAYING_NOW_NOTIFICATION_ID, notification)
            isNotificationDisplayed = true
        }
    }

    private fun stopForeground() {
        stopForeground(true)
        isNotificationDisplayed = false
    }

    private fun inject() {
        application.cast<Application>().daggerComponent.playerServiceComponentBuilder()
            .build()
            .inject(this)
    }

    private fun launchImmediately(block: suspend CoroutineScope.() -> Unit) {
        GlobalScope.launch(
            context = Dispatchers.Unconfined,
            start = CoroutineStart.UNDISPATCHED,
            block = block
        )
    }

    private object NotificationConst {
        const val PLAYING_NOW_CHANNEL_ID = "playing_now"
        const val PLAYING_NOW_NOTIFICATION_ID = 1
    }
}
