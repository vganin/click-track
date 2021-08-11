package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsHeight
import com.vsevolodganin.clicktrack.utils.compose.Const.FAB_SIZE_WITH_PADDINGS

@Composable
fun PadWithFabSpace() {
    Spacer(modifier = Modifier.navigationBarsHeight(additional = FAB_SIZE_WITH_PADDINGS))
}

fun LazyListScope.padWithFabSpace() {
    item {
        PadWithFabSpace()
    }
}

private object Const {
    // Should equal to FabSize + 2 * ExtendedFabIconPadding
    val FAB_SIZE_WITH_PADDINGS = 80.dp
}
