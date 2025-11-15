package com.vsevolodganin.clicktrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.ui.screen.ClickTrackListScreenPreview

@Composable
fun ClickTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkScheme else LightScheme,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(8.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(8.dp),
            extraLarge = RoundedCornerShape(8.dp),
        ),
    ) {
        content()
    }
}

val LightScheme = lightColorScheme(
    primary = SignatureColors.BloodRed,
    onPrimary = SignatureColors.BoneWhite,
    primaryContainer = SignatureColors.BloodRed,
    onPrimaryContainer = SignatureColors.BoneWhite,
    secondary = SignatureColors.BloodRed,
    onSecondary = SignatureColors.BoneWhite,
    secondaryContainer = SignatureColors.BloodRed,
    onSecondaryContainer = SignatureColors.BoneWhite,
    tertiary = SignatureColors.BloodRed,
    onTertiary = SignatureColors.BoneWhite,
    tertiaryContainer = SignatureColors.BloodRed,
    onTertiaryContainer = SignatureColors.BoneWhite,
    error = SignatureColors.BloodRed,
    onError = SignatureColors.BoneWhite,
    errorContainer = SignatureColors.BloodRed,
    onErrorContainer = SignatureColors.BoneWhite,
    background = SignatureColors.BoneWhite,
    onBackground = SignatureColors.AshBlack,
    surface = SignatureColors.BoneWhite,
    onSurface = SignatureColors.AshBlack,
    surfaceVariant = SignatureColors.BoneWhite,
    onSurfaceVariant = lerp(SignatureColors.AshBlack, SignatureColors.BoneWhite, 0.3f),
    outline = lerp(SignatureColors.BoneWhite, SignatureColors.AshBlack, 0.25f),
    outlineVariant = lerp(SignatureColors.BoneWhite, SignatureColors.AshBlack, 0.15f),
    scrim = Color.Black,
    inverseSurface = SignatureColors.AshBlack,
    inverseOnSurface = SignatureColors.BoneWhite,
    inversePrimary = SignatureColors.BloodRed,
    surfaceDim = lerp(SignatureColors.BoneWhite, SignatureColors.AshBlack, 0.15f),
    surfaceBright = SignatureColors.BoneWhite,
    surfaceContainerLowest = SignatureColors.BoneWhite,
    surfaceContainerLow = lerp(SignatureColors.BoneWhite, SignatureColors.AshBlack, 0.02f),
    surfaceContainer = lerp(SignatureColors.BoneWhite, SignatureColors.AshBlack, 0.04f),
    surfaceContainerHigh = lerp(SignatureColors.BoneWhite, SignatureColors.AshBlack, 0.06f),
    surfaceContainerHighest = lerp(SignatureColors.BoneWhite, SignatureColors.AshBlack, 0.08f),
)

val DarkScheme = darkColorScheme(
    primary = SignatureColors.BloodRedDark,
    onPrimary = SignatureColors.BoneWhite,
    primaryContainer = SignatureColors.BloodRedDark,
    onPrimaryContainer = SignatureColors.BoneWhite,
    secondary = SignatureColors.BloodRedDark,
    onSecondary = SignatureColors.BoneWhite,
    secondaryContainer = SignatureColors.BloodRedDark,
    onSecondaryContainer = SignatureColors.BoneWhite,
    tertiary = SignatureColors.BloodRedDark,
    onTertiary = SignatureColors.BoneWhite,
    tertiaryContainer = SignatureColors.BloodRedDark,
    onTertiaryContainer = SignatureColors.BoneWhite,
    error = SignatureColors.BloodRedDark,
    onError = SignatureColors.BoneWhite,
    errorContainer = SignatureColors.BloodRedDark,
    onErrorContainer = SignatureColors.BoneWhite,
    background = SignatureColors.AshBlack,
    onBackground = SignatureColors.BoneWhite,
    surface = SignatureColors.AshBlack,
    onSurface = SignatureColors.BoneWhite,
    surfaceVariant = SignatureColors.AshBlack,
    onSurfaceVariant = lerp(SignatureColors.BoneWhite, SignatureColors.AshBlack, 0.3f),
    outline = lerp(SignatureColors.AshBlack, SignatureColors.BoneWhite, 0.25f),
    outlineVariant = lerp(SignatureColors.AshBlack, SignatureColors.BoneWhite, 0.15f),
    scrim = Color.Black,
    inverseSurface = SignatureColors.BoneWhite,
    inverseOnSurface = SignatureColors.AshBlack,
    inversePrimary = SignatureColors.BloodRedDark,
    surfaceDim = SignatureColors.AshBlack,
    surfaceBright = lerp(SignatureColors.AshBlack, SignatureColors.BoneWhite, 0.15f),
    surfaceContainerLowest = SignatureColors.AshBlack,
    surfaceContainerLow = lerp(SignatureColors.AshBlack, SignatureColors.BoneWhite, 0.02f),
    surfaceContainer = lerp(SignatureColors.AshBlack, SignatureColors.BoneWhite, 0.04f),
    surfaceContainerHigh = lerp(SignatureColors.AshBlack, SignatureColors.BoneWhite, 0.06f),
    surfaceContainerHighest = lerp(SignatureColors.AshBlack, SignatureColors.BoneWhite, 0.08f),
)

private object SignatureColors {
    val BloodRed = Color(0xFFA53030)
    val BloodRedDark = Color(0xFF982D2D)
    val BoneWhite = Color(0xFFEFEEEE)
    val AshBlack = Color(0xFF1B1A1A)
}

@Composable
internal fun ClickTrackThemePreview() {
    ClickTrackTheme {
        ClickTrackListScreenPreview()
    }
}
