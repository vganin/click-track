package net.ganin.vsevolod.clicktrack.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ganin.vsevolod.clicktrack.Application
import net.ganin.vsevolod.clicktrack.MainActivity
import net.ganin.vsevolod.clicktrack.R
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.player.PlayerService.NotificationConst.DEFAULT_CHANNEL_ID
import net.ganin.vsevolod.clicktrack.player.PlayerService.NotificationConst.DEFAULT_NOTIFICATION_ID
import net.ganin.vsevolod.clicktrack.utils.cast
import javax.inject.Inject
import kotlin.random.Random

class PlayerService : Service() {

    companion object {
        fun start(context: Context, clickTrack: ClickTrackWithId) {
            val intent = serviceIntent(context).apply {
                putExtra(EXTRA_KEY_CLICK_TRACK, clickTrack)
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            context.startService(serviceIntent(context).apply {
                action = ACTION_STOP
            })
            context.stopService(serviceIntent(context))
        }

        fun bind(context: Context, serviceConnection: ServiceConnection) {
            if (!context.bindService(serviceIntent(context), serviceConnection, BIND_AUTO_CREATE)) {
                throw RuntimeException("Wasn't able to connect to player service")
            }
        }

        private fun serviceIntent(context: Context): Intent = Intent(context, PlayerService::class.java)

        private const val EXTRA_KEY_CLICK_TRACK = "click_track"
        private const val ACTION_STOP = "stop"
    }

    @Inject
    lateinit var player: Player

    override fun onCreate() {
        super.onCreate()
        inject()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == ACTION_STOP) {
            stopPlayer()
            stopForeground()
            stopSelf()
            return START_NOT_STICKY
        }

        val clickTrack: ClickTrackWithId = intent.getParcelableExtra(EXTRA_KEY_CLICK_TRACK)
            ?: throw RuntimeException("No click track to play")

        startPlayer(clickTrack)
        startForeground(clickTrack)

        return START_STICKY
    }

    override fun onDestroy() {
        stopPlayer()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder = PlayerServiceBinder(player.playbackState())

    private fun startPlayer(clickTrack: ClickTrackWithId) {
        GlobalScope.launch(Dispatchers.Unconfined) {
            player.play(clickTrack)
            stopForeground()
            stopSelf()
        }
    }

    private fun stopPlayer() {
        GlobalScope.launch(Dispatchers.Unconfined) {
            player.stop()
        }
    }

    private fun startForeground(clickTrack: ClickTrackWithId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DEFAULT_CHANNEL_ID,
                getString(R.string.notification_channel_default),
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val launchAppIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        val stopServiceIntent = PendingIntent.getService(this, 0, serviceIntent(this).apply { action = ACTION_STOP }, 0)

        val notification: Notification = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.notification_playing_now))
            .setContentText(clickTrack.value.name)
            .setContentIntent(launchAppIntent)
            .setDeleteIntent(stopServiceIntent)
            .addAction(R.mipmap.ic_launcher, getString(R.string.notification_stop), stopServiceIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
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

    private object NotificationConst {
        const val DEFAULT_CHANNEL_ID = "default"
        val DEFAULT_NOTIFICATION_ID = Random.nextInt(Int.MAX_VALUE) + 1
    }
}
