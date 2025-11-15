package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.vsevolodganin.clicktrack.ui.theme.DarkScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarColors(
            containerColor = DarkScheme.surface,
            scrolledContainerColor = DarkScheme.surfaceContainer,
            navigationIconContentColor = DarkScheme.onSurface,
            titleContentColor = DarkScheme.onSurface,
            actionIconContentColor = DarkScheme.onSurfaceVariant,
            subtitleContentColor = DarkScheme.onSurfaceVariant,
        ),
    )
}
