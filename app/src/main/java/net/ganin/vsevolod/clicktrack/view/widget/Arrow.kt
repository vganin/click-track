package net.ganin.vsevolod.clicktrack.view.widget

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.graphics.vector.VectorAssetBuilder
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.ganin.vsevolod.clicktrack.view.widget.ArrowDirection.DOWN
import net.ganin.vsevolod.clicktrack.view.widget.ArrowDirection.LEFT
import net.ganin.vsevolod.clicktrack.view.widget.ArrowDirection.RIGHT
import net.ganin.vsevolod.clicktrack.view.widget.ArrowDirection.UP

enum class ArrowDirection {
    UP, DOWN, LEFT, RIGHT
}

@Composable
fun Arrow(
    direction: ArrowDirection,
    modifier: Modifier = Modifier,
    tint: Color = AmbientContentColor.current
) {
    val vectorAsset = arrowVectorResource(direction)
    Icon(asset = vectorAsset, modifier = modifier, tint = tint)
}

@Composable
fun arrowVectorResource(direction: ArrowDirection): VectorAsset {
    return remember(direction) {
        VectorAssetBuilder(defaultWidth = 24.dp, defaultHeight = 12.dp, viewportWidth = 2f, viewportHeight = 2f)
            .addPath(
                pathData = when (direction) {
                    UP -> PathBuilder()
                        .moveTo(0f, 2f)
                        .lineTo(1f, 0f)
                        .lineTo(2f, 2f)
                        .close()
                        .getNodes()
                    DOWN -> PathBuilder()
                        .moveTo(0f, 0f)
                        .lineTo(2f, 0f)
                        .lineTo(1f, 2f)
                        .close()
                        .getNodes()
                    LEFT -> PathBuilder()
                        .moveTo(2f, 0f)
                        .lineTo(0f, 1f)
                        .lineTo(2f, 2f)
                        .close()
                        .getNodes()
                    RIGHT -> PathBuilder()
                        .moveTo(0f, 0f)
                        .lineTo(2f, 1f)
                        .lineTo(0f, 2f)
                        .close()
                        .getNodes()
                },
                fill = SolidColor(Color.Black)
            )
            .build()
    }
}

@Preview
@Composable
fun PreviewArrow() {
    Row {
        Arrow(LEFT)
        Arrow(UP)
        Arrow(RIGHT)
        Arrow(DOWN)
    }
}
