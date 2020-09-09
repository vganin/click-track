package net.ganin.vsevolod.clicktrack.view

import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.ClickTrack

@Composable
fun ClickTrackAndPlayView(
    clickTrack: ClickTrack,
    onPlayToggle: (Boolean) -> Unit
) = ConstraintLayout(
    modifier = Modifier.fillMaxSize()
) {
    ClickTrackView(clickTrack)

    var isPlaying by remember { mutableStateOf(false) }
    FloatingActionButton(
        onClick = {
            isPlaying = !isPlaying
            onPlayToggle(isPlaying)
        },
        modifier = Modifier.constrainAs(createRef()) {
            centerHorizontallyTo(parent)
            bottom.linkTo(parent.bottom, margin = 16.dp)
        }
    ) {
        val iconResourceId = if (isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        Icon(asset = imageResource(iconResourceId))
    }
}

@Preview
@Composable
fun PreviewClickTrackAndPlayView() {
    ClickTrackAndPlayView(PREVIEW_CLICK_TRACK, onPlayToggle = {})
}