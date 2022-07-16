package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import com.vsevolodganin.clicktrack.R

@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
) {
    com.google.accompanist.insets.ui.TopAppBar(
        title = title,
        modifier = modifier,
        contentPadding = WindowInsets.statusBars.only(WindowInsetsSides.Top).asPaddingValues(),
        navigationIcon = navigationIcon,
        actions = actions,
        backgroundColor = MaterialTheme.colors.darkAppBar,
        contentColor = MaterialTheme.colors.onDarkAppBarSurface,
        elevation = elevation
    )
}

val Colors.darkAppBar: Color
    @Composable
    get() = if (isLight) colorResource(R.color.dark_app_bar) else surface

val Colors.onDarkAppBarSurface: Color
    @Composable
    get() = if (isLight) colorResource(R.color.on_dark_app_bar) else onSurface
