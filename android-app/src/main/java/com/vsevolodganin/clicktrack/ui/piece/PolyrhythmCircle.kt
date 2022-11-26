package com.vsevolodganin.clicktrack.ui.piece

import android.annotation.SuppressLint
import android.graphics.Matrix
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.SweepGradientShader
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.vsevolodganin.clicktrack.ui.ClickTrackTheme
import com.vsevolodganin.clicktrack.utils.compose.Angle
import com.vsevolodganin.clicktrack.utils.compose.AngleSector
import com.vsevolodganin.clicktrack.utils.compose.FULL_ANGLE_DEGREES
import com.vsevolodganin.clicktrack.utils.compose.toRadians
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PolyrhythmCircle(
    outerDotNumber: Int,
    innerDotNumber: Int,
    modifier: Modifier = Modifier,
    progressAngle: Float? = null,
    progressVelocity: Float = 15f, // degrees per second
) {
    val primaryColor = MaterialTheme.colors.primary
    val secondaryColor = MaterialTheme.colors.secondary

    val innerDotAnimations = animatedDots(number = innerDotNumber, progressAngle = progressAngle)
    val outerDotAnimations = animatedDots(number = outerDotNumber, progressAngle = progressAngle)

    val sweepAngle = remember { Animatable(0f) }
    LaunchedEffect(progressAngle == null) {
        if (progressAngle == null) {
            sweepAngle.snapTo(0f)
        } else {
            val duration = (PROGRESS_LINE_MAX_SWEEP_ANGLE_DEGREES * 1000f / progressVelocity).roundToInt()
            sweepAngle.animateTo(
                PROGRESS_LINE_MAX_SWEEP_ANGLE_DEGREES,
                tween(duration, easing = LinearEasing)
            )
        }
    }

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val outerCircleRadius = size.maxDimension / 2.0f
        val innerCircleRadius = size.maxDimension / 4.0f

        drawPolyrhythmCircle(
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            innerDotNumber = innerDotNumber,
            innerCircleRadius = innerCircleRadius,
            innerDotAnimations = innerDotAnimations,
            outerDotNumber = outerDotNumber,
            outerCircleRadius = outerCircleRadius,
            outerDotAnimations = outerDotAnimations,
        )

        if (progressAngle != null) {
            val color = secondaryColor
            val start = center
            val end = start + Offset(
                x = cos(progressAngle.toRadians()) * outerCircleRadius,
                y = sin(progressAngle.toRadians()) * outerCircleRadius
            )
            val strokeWidth = 2.dp.toPx()

            rotate(-90f) {
                drawLine(
                    color = color,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )

                rotate(progressAngle - sweepAngle.value) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            0f to Color.Transparent,
                            sweepAngle.value / FULL_ANGLE_DEGREES to color.copy(alpha = 0.7f)
                        ),
                        startAngle = 0f,
                        sweepAngle = sweepAngle.value,
                        useCenter = true,
                        style = Fill,
                        topLeft = center - Offset(outerCircleRadius, outerCircleRadius),
                        size = Size(outerCircleRadius * 2, outerCircleRadius * 2),
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawPolyrhythmCircle(
    primaryColor: Color,
    secondaryColor: Color,
    innerDotNumber: Int,
    innerCircleRadius: Float,
    innerDotAnimations: List<DotAnimation>,
    outerDotNumber: Int,
    outerCircleRadius: Float,
    outerDotAnimations: List<DotAnimation>,
) {
    drawCircleWithMarks(
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        number = innerDotNumber,
        circleRadius = innerCircleRadius,
    )

    drawDotAnimations(
        color = secondaryColor,
        dotAnimations = innerDotAnimations,
        radius = innerCircleRadius
    )

    drawCircleWithMarks(
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        number = outerDotNumber,
        circleRadius = outerCircleRadius,
    )

    drawDotAnimations(
        color = secondaryColor,
        dotAnimations = outerDotAnimations,
        radius = outerCircleRadius
    )
}

private fun DrawScope.drawCircleWithMarks(
    primaryColor: Color,
    secondaryColor: Color,
    number: Int,
    circleRadius: Float,
) {
    val brush = sweepGradient(
        widthFraction = 0.08f,
        centerColor = secondaryColor,
        borderColor = primaryColor.copy(alpha = 0.1f)
    )
    val angleIncrement = FULL_ANGLE_DEGREES / number

    for (i in 0 until number) {
        val angle = angleIncrement * i

        rotate(angle - 90f) {
            drawArc(
                brush = brush,
                startAngle = -angleIncrement / 2,
                sweepAngle = angleIncrement,
                useCenter = false,
                style = Stroke(width = STROKE_WIDTH.toPx()),
                topLeft = center - Offset(circleRadius, circleRadius),
                size = Size(circleRadius * 2, circleRadius * 2)
            )

            drawCircle(
                brush = brush,
                radius = DOT_RADIUS.toPx(),
                center = center + Offset(x = circleRadius, y = 0f)
            )
        }
    }
}

private fun DrawScope.drawDotAnimations(
    color: Color,
    dotAnimations: List<DotAnimation>,
    radius: Float,
) {
    for (dotAnimation in dotAnimations) {
        val center = center + Offset(x = radius, y = 0f)
        val animationProgress = dotAnimation.animationProgress.value
        val scale = lerp(1f, 3f, animationProgress)
        val alpha = lerp(0.5f, 0f, animationProgress)
        val modulatedColor = color.copy(alpha = color.alpha * alpha)

        withTransform(transformBlock = {
            rotate(dotAnimation.angle - 90f)
            scale(scale = scale, pivot = center)
        }) {
            drawCircle(
                color = modulatedColor,
                radius = DOT_RADIUS.toPx(),
                center = center
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedAnimatable")
@Composable
private fun animatedDots(
    number: Int,
    progressAngle: Float?,
): List<DotAnimation> {
    val coroutineScope = rememberCoroutineScope()
    val animations = remember {
        mutableStateListOf<DotAnimation>()
    }

    if (progressAngle == null) return animations

    val previousProgressAngle = remember { mutableStateOf(progressAngle) }

    val angleIncrement = FULL_ANGLE_DEGREES / number
    for (i in 0 until number) {
        val dotAngle = angleIncrement * i
        if (Angle(dotAngle) in AngleSector(previousProgressAngle.value, progressAngle)) {
            val progressAnimatable = Animatable(0f)

            val dotAnimation = DotAnimation(
                angle = dotAngle,
                animationProgress = progressAnimatable.asState()
            )
            animations += dotAnimation

            coroutineScope.launch {
                progressAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
                animations -= dotAnimation
            }
        }
    }

    previousProgressAngle.value = progressAngle

    return animations
}

private class DotAnimation(
    val angle: Float,
    val animationProgress: State<Float>,
)

private fun sweepGradient(widthFraction: Float, centerColor: Color, borderColor: Color): Brush {
    val centerFraction = 0.5f
    return object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val center = size.center
            return SweepGradientShader(
                center = center,
                colors = listOf(borderColor, centerColor, borderColor),
                colorStops = listOf(centerFraction - widthFraction, centerFraction, centerFraction + widthFraction)
            ).apply {
                setLocalMatrix(Matrix().apply {
                    postRotate(180f, center.x, center.y)
                })
            }
        }
    }
}

private val STROKE_WIDTH = 8.dp
private val DOT_RADIUS = 8.dp
private const val PROGRESS_LINE_MAX_SWEEP_ANGLE_DEGREES = 15f

@Preview
@Composable
private fun Preview() {
    ClickTrackTheme {
        PolyrhythmCircle(
            outerDotNumber = 3,
            innerDotNumber = 4,
            progressAngle = 30f,
            modifier = Modifier.size(100.dp)
        )
    }
}
