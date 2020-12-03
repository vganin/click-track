package net.ganin.vsevolod.clicktrack.utils.compose

import kotlin.math.PI

private const val RADIANS_TO_DEGREES = (180.0 / PI).toFloat()

fun Float.toDegrees(): Float = this * RADIANS_TO_DEGREES
fun Double.toDegrees(): Double = this * RADIANS_TO_DEGREES

fun Float.toRadians(): Float = this / 180f * PI.toFloat()
fun Double.toRadians(): Double = this / 180 * PI
