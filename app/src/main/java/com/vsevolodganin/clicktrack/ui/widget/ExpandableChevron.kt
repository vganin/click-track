package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableChevron(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
) {
    val animatedProgress by animateFloatAsState(if (isExpanded) 1f else 0f)

    val collapsedPathNodes = remember { addPathNodes(COLLAPSED_PATH_DATA) }
    val expandedPathNodes = remember { addPathNodes(EXPANDED_PATH_DATA) }

    val morphedPathNodes = lerp(collapsedPathNodes, expandedPathNodes, animatedProgress)

    Icon(
        imageVector = ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).addPath(pathData = morphedPathNodes, fill = SolidColor(Color.White)).build(),
        contentDescription = null,
        modifier = modifier,
        tint = tint,
    )
}

private fun lerp(
    fromPathNodes: List<PathNode>,
    toPathNodes: List<PathNode>,
    t: Float,
): List<PathNode> {
    return fromPathNodes.mapIndexed { i, from ->
        val to = toPathNodes[i]
        if (from is PathNode.MoveTo && to is PathNode.MoveTo) {
            PathNode.MoveTo(
                androidx.compose.ui.util.lerp(from.x, to.x, t),
                androidx.compose.ui.util.lerp(from.y, to.y, t),
            )
        } else if (from is PathNode.LineTo && to is PathNode.LineTo) {
            PathNode.LineTo(
                androidx.compose.ui.util.lerp(from.x, to.x, t),
                androidx.compose.ui.util.lerp(from.y, to.y, t),
            )
        } else {
            // We only support MoveTo and LineTo commands in this demo for brevity.
            throw IllegalStateException("Unsupported SVG PathNode command")
        }
    }
}

private const val COLLAPSED_PATH_DATA = "M 12 13.17 L 7.41 8.59 L 6 10 L 12 16 L 18 10 L 16.59 8.59 L 12 13.17"
private const val EXPANDED_PATH_DATA = "M 12 8 L 6 14 L 7.41 15.41 L 12 10.83 L 16.59 15.41 L 18 14 L 12 8"

@Preview
@Composable
private fun Preview() {
    ExpandableChevron(false)
}
