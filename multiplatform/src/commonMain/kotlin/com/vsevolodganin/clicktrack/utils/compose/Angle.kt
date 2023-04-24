package com.vsevolodganin.clicktrack.utils.compose

import kotlin.math.PI

const val FULL_ANGLE_DEGREES = 360f
const val HALF_ANGLE_DEGREES = 180f
const val RADIANS_TO_DEGREES = (HALF_ANGLE_DEGREES / PI).toFloat()

fun Float.toDegrees(): Float = this * RADIANS_TO_DEGREES
fun Float.toRadians(): Float = this / HALF_ANGLE_DEGREES * PI.toFloat()

class Angle(val degrees: Float) {

    val radians: Float
        get() = degrees.toRadians()

    // Degrees in [0, 360f) range
    val normalizedDegrees: Float
        get() = (degrees % FULL_ANGLE_DEGREES).let {
            if (it < 0f) {
                FULL_ANGLE_DEGREES + it
            } else {
                it
            }
        }
}

class AngleSector(private val start: Angle, private val end: Angle) {

    constructor(startDegrees: Float, endDegrees: Float) : this(Angle(startDegrees), Angle(endDegrees))

    operator fun contains(angle: Angle): Boolean {
        val startNormalized = start.normalizedDegrees
        val endNormalized = end.normalizedDegrees
        val angleNormalized = angle.normalizedDegrees

        return if (startNormalized > endNormalized) {
            angleNormalized in startNormalized..(endNormalized + FULL_ANGLE_DEGREES) ||
                    (angleNormalized + FULL_ANGLE_DEGREES) in startNormalized..(endNormalized + FULL_ANGLE_DEGREES)
        } else {
            angleNormalized in startNormalized..endNormalized
        }
    }
}
