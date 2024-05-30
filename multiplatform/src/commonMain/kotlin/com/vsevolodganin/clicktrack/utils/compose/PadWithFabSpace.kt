package com.vsevolodganin.clicktrack.utils.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PadWithFabSpace() {
    Spacer(
        modifier = Modifier
            .navigationBarsPadding()
            .height(Const.FAB_SIZE_WITH_PADDINGS),
    )
}

fun Any.padWithFabSpace() {
    // FIXME(https://github.com/JetBrains/compose-multiplatform/issues/3087): Workaround for compiler bug
    this as LazyListScope
    item {
        PadWithFabSpace()
    }
}

private object Const {
    // Should equal to FabSize + 2 * ExtendedFabIconPadding
    val FAB_SIZE_WITH_PADDINGS = 80.dp
}
