package com.vsevolodganin.clicktrack

import android.Manifest
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import com.vsevolodganin.clicktrack.common.InAppReview
import com.vsevolodganin.clicktrack.migration.MigrationManager
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooser
import com.vsevolodganin.clicktrack.ui.RootView
import com.vsevolodganin.clicktrack.utils.android.PermissionsHelper
import com.vsevolodganin.clicktrack.utils.cast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var intentProcessor: IntentProcessor

    @Inject
    lateinit var migrationManager: MigrationManager

    @Inject
    lateinit var rootViewModel: RootViewModel

    @Inject
    lateinit var inAppReview: InAppReview

    @Inject
    lateinit var permissionsHelper: PermissionsHelper

    @Suppress("unused") // FIXME: Initializing eagerly for optimisation
    @Inject
    lateinit var playerServiceAccess: PlayerServiceAccess

    @Suppress("unused") // FIXME: Initializing eagerly for proper registration of Activity Result API
    @Inject
    lateinit var soundChooser: SoundChooser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen()

        inject()

        migrationManager.tryMigrate()

        setContent {
            RootView(rootViewModel)
        }

        if (savedInstanceState == null) {
            intentProcessor.process(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GlobalScope.launch {
                permissionsHelper.requestPermission(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentProcessor.process(intent)
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
        inAppReview.tryLaunchRequestReview()
    }

    private fun inject() {
        application.cast<MainApplication>().daggerComponent.activityComponentBuilder()
            .activity(this)
            .rootComponentContext(defaultComponentContext())
            .build()
            .inject(this)
    }
}
