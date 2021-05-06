package com.vsevolodganin.clicktrack.player

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Parcelable
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.vsevolodganin.clicktrack.Application
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.intentForLaunchAppWithClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.player.PlayerService.NotificationConst.DEFAULT_NOTIFICATION_ID
import com.vsevolodganin.clicktrack.player.PlayerService.NotificationConst.PLAYING_NOW_CHANNEL_ID
import com.vsevolodganin.clicktrack.utils.cast
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class PlayerService : Service() {

    @Parcelize
    data class StartArguments(
        val clickTrack: ClickTrackWithId,
        val startAtProgress: Double?,
    ) : Parcelable

    companion object {
        fun start(context: Context, arguments: StartArguments) {
            val intent = serviceIntent(context).apply {
                action = ACTION_START
                putExtra(EXTRA_KEY_ARGUMENTS, arguments)
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

        private const val EXTRA_KEY_ARGUMENTS = "arguments"
        private const val ACTION_START = "start"
        private const val ACTION_STOP = "stop"
        private const val ACTION_PAUSE = "pause"
    }

    @Inject
    lateinit var player: Player

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    override fun onCreate() {
        super.onCreate()

        inject()

        launchUndispatched {
            player.playbackState().drop(1).collect {
                if (it == null) {
                    stopForeground()
                    stopSelf()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return when (intent.action) {
            ACTION_START -> {
                val args: StartArguments = intent.getParcelableExtra(EXTRA_KEY_ARGUMENTS)
                    ?: throw RuntimeException("No start arguments were supplied")

                startPlayer(args)
                startForeground(args.clickTrack)

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
            else -> throw IllegalArgumentException("Illegal action: ${intent.action}")
        }
    }

    override fun onDestroy() {
        stopPlayer()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder = PlayerServiceBinder(player.playbackState())

    private fun startPlayer(args: StartArguments) {
        launchUndispatched {
            player.start(args.clickTrack, args.startAtProgress)
        }
    }

    private fun pausePlayer() {
        launchUndispatched {
            player.pause()
        }
    }

    private fun stopPlayer() {
        launchUndispatched {
            player.stop()
        }
    }

    private fun startForeground(clickTrack: ClickTrackWithId) {
        val channel = NotificationChannelCompat.Builder(PLAYING_NOW_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_MIN)
            .setName(getString(R.string.notification_channel_playing_now))
            .build()
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(channel)

        val launchAppIntent = PendingIntent.getActivity(this, 0, intentForLaunchAppWithClickTrack(this, clickTrack), PendingIntent.FLAG_UPDATE_CURRENT)
        val stopServiceIntent = PendingIntent.getService(this, 0, serviceIntent(this).apply { action = ACTION_STOP }, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, PLAYING_NOW_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ResourcesCompat.getColor(resources, R.color.secondary_dark, null))
            .setContentTitle(getString(R.string.notification_playing_now))
            .setContentText(clickTrack.value.name)
            .setContentIntent(launchAppIntent)
            .setDeleteIntent(stopServiceIntent)
            .addAction(0, getString(R.string.notification_stop), stopServiceIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build()

        startForeground(DEFAULT_NOTIFICATION_ID, notification)
    }

    private fun stopForeground() {
        stopForeground(true)
    }

    private fun inject() {
        application.cast<Application>().daggerComponent.playerServiceComponentBuilder()
            .build()
            .inject(this)
    }

    private fun launchUndispatched(block: suspend CoroutineScope.() -> Unit) {
        GlobalScope.launch(
            context = Dispatchers.Unconfined,
            start = CoroutineStart.UNDISPATCHED,
            block = block
        )
    }

    private object NotificationConst {
        const val PLAYING_NOW_CHANNEL_ID = "playing_now"
        val DEFAULT_NOTIFICATION_ID = Random.nextInt(Int.MAX_VALUE) + 1
    }
}
