package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun InsetsAwareTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.statusBars,
            applyStart = true,
            applyTop = true,
            applyEnd = true,
        ),
        navigationIcon = navigationIcon,
        actions = actions,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation

    )
}
