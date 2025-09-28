package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        backgroundColor = MaterialTheme.colors.darkAppBar,
        contentColor = MaterialTheme.colors.onDarkAppBarSurface,
        elevation = elevation,
    )
}

val Colors.darkAppBar: Color
    @Composable
    get() = if (isLight) Color(0xFF262525) else surface

val Colors.onDarkAppBarSurface: Color
    @Composable
    get() = if (isLight) Color.White else onSurface

@Composable
internal expect fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier,
    navigationIcon: @Composable (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    elevation: Dp,
)
