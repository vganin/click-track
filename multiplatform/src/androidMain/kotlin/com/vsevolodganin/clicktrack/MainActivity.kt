package com.vsevolodganin.clicktrack

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import com.vsevolodganin.clicktrack.common.InAppReview
import com.vsevolodganin.clicktrack.migration.MigrationManager
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooser
import com.vsevolodganin.clicktrack.ui.RootView
import com.vsevolodganin.clicktrack.utils.android.PermissionsHelper
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    @Suppress("unused") // FIXME: Initializing eagerly for proper registration of Activity Result API
    @Inject
    private lateinit var soundsChooser: SoundChooser

    @Inject
    private lateinit var permissionsHelper: PermissionsHelper

    @Inject
    private lateinit var migrationManager: MigrationManager

    @Inject
    private lateinit var rootViewModel: RootViewModel

    @Inject
    private lateinit var intentProcessor: IntentProcessor

    @Inject
    private lateinit var inAppReview: InAppReview

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applicationComponent.mainActivityComponentFactory
            .create(
                activity = this,
                componentContext = defaultComponentContext(),
            )
            .inject(this)

        installSplashScreen()

        enableEdgeToEdge()

        migrationManager.tryMigrate()

        setContent {
            val isSystemInDarkTheme = isSystemInDarkTheme()

            DisposableEffect(isSystemInDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
                    navigationBarStyle = if (isSystemInDarkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    },
                )
                onDispose {}
            }

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
}
