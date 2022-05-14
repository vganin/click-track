package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PadWithFabSpace() {
    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
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
