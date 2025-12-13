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
import com.vsevolodganin.clicktrack.di.component.MainActivityComponent
import com.vsevolodganin.clicktrack.ui.RootView
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var component: MainActivityComponent

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        enableEdgeToEdge()

        component = createGraphFactory<MainActivityComponent.Factory>().create(
            applicationComponent = applicationComponent,
            activity = this,
            componentContext = defaultComponentContext(),
        )

        // FIXME: Initializing eagerly for proper registration of Activity Result API
        component.soundChooser
        component.permissionsHelper

        component.migrationManager.tryMigrate()

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

            RootView(component.rootViewModel)
        }

        if (savedInstanceState == null) {
            component.intentProcessor.process(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GlobalScope.launch {
                component.permissionsHelper.requestPermission(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        component.intentProcessor.process(intent)
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
        component.inAppReview.tryLaunchRequestReview()
    }
}
