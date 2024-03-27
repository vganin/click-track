package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.ui.piece.ArrowDirection.DOWN
import com.vsevolodganin.clicktrack.ui.piece.ArrowDirection.LEFT
import com.vsevolodganin.clicktrack.ui.piece.ArrowDirection.RIGHT
import com.vsevolodganin.clicktrack.ui.piece.ArrowDirection.UP
import com.vsevolodganin.clicktrack.utils.compose.Preview

enum class ArrowDirection {
    UP, DOWN, LEFT, RIGHT
}

@Composable
fun Arrow(
    direction: ArrowDirection,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        imageVector = arrowVectorResource(direction),
        contentDescription = null,
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun arrowVectorResource(direction: ArrowDirection): ImageVector {
    return remember(direction) {
        ImageVector.Builder(defaultWidth = 24.dp, defaultHeight = 12.dp, viewportWidth = 2f, viewportHeight = 2f)
            .addPath(
                pathData = when (direction) {
                    UP -> PathBuilder()
                        .moveTo(0f, 2f)
                        .lineTo(1f, 0f)
                        .lineTo(2f, 2f)
                        .close()
                        .nodes

                    DOWN -> PathBuilder()
                        .moveTo(0f, 0f)
                        .lineTo(2f, 0f)
                        .lineTo(1f, 2f)
                        .close()
                        .nodes

                    LEFT -> PathBuilder()
                        .moveTo(2f, 0f)
                        .lineTo(0f, 1f)
                        .lineTo(2f, 2f)
                        .close()
                        .nodes

                    RIGHT -> PathBuilder()
                        .moveTo(0f, 0f)
                        .lineTo(2f, 1f)
                        .lineTo(0f, 2f)
                        .close()
                        .nodes
                },
                fill = SolidColor(Color.Black)
            )
            .build()
    }
}

@Preview
@Composable
private fun Preview() {
    Row {
        Arrow(LEFT)
        Arrow(UP)
        Arrow(RIGHT)
        Arrow(DOWN)
    }
}
