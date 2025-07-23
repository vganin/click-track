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
import com.vsevolodganin.clicktrack.di.component.MainActivityComponent
import com.vsevolodganin.clicktrack.di.component.create
import com.vsevolodganin.clicktrack.ui.RootView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var component: MainActivityComponent

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen()

        component = MainActivityComponent::class.create(
            applicationComponent = applicationComponent,
            activity = this,
            componentContext = defaultComponentContext(),
        )

        // FIXME: Initializing eagerly for proper registration of Activity Result API
        component.soundChooser
        component.permissionsHelper

        component.migrationManager.tryMigrate()

        setContent {
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
