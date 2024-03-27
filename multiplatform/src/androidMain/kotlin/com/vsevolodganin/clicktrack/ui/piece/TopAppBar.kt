@file:JvmName("AndroidTopAppBarKt")

package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.vsevolodganin.clicktrack.utils.compose.statusBars

@Composable
internal actual fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier,
    navigationIcon: @Composable (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    elevation: Dp
) {
    androidx.compose.material.TopAppBar(
        title = title,
        windowInsets = WindowInsets.statusBars.only(WindowInsetsSides.Top),
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation
    )
}
