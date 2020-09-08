package net.ganin.vsevolod.clicktrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.ganin.vsevolod.clicktrack.audio.ClickTrackPlayer
import net.ganin.vsevolod.clicktrack.lib.*
import net.ganin.vsevolod.clicktrack.view.ContentView
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val mainScope = MainScope()

    private val clickTrackPlayerDispatcher = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "click_track").apply {
            priority = Thread.MAX_PRIORITY
        }
    }.asCoroutineDispatcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val testClickTrack = ClickTrack(
            cues = listOf(
                CueWithDuration(
                    duration = CueDuration.Beats(4),
                    cue = Cue(
                        bpm = 100,
                        timeSignature = TimeSignature(3, 4)
                    )
                ),
                CueWithDuration(
                    duration = CueDuration.Beats(8),
                    cue = Cue(
                        bpm = 200,
                        timeSignature = TimeSignature(3, 4)
                    )
                ),
            ),
            loop = true
        )

        setContent {
            ContentView(testClickTrack)
        }

        mainScope.launch(clickTrackPlayerDispatcher) {
            ClickTrackPlayer(this@MainActivity).play(testClickTrack)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
        clickTrackPlayerDispatcher.close()
    }
}
