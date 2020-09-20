package net.ganin.vsevolod.clicktrack.utils.compose

import androidx.compose.ui.geometry.Offset
import kotlin.math.atan

val Offset.vectorAngle: Float get() {
    return atan(y / x).toDegrees()
}
