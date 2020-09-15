package net.ganin.vsevolod.clicktrack.view

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.lib.ClickTrack

@Composable
fun ClickTrackListView(
    items: List<ClickTrack>,
    modifier: Modifier = Modifier
) {
    LazyColumnFor(items, modifier) { clickTrack ->
        ClickTrackView(
            clickTrack,
            modifier = Modifier
                .height(100.dp)
                .fillParentMaxWidth()
        )
    }
}

@Preview
@Composable
fun PreviewClickTrackListView() {
    ClickTrackListView(
        listOf(
            PREVIEW_CLICK_TRACK_1,
            PREVIEW_CLICK_TRACK_2,
        )
    )
}
