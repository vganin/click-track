package net.ganin.vsevolod.clicktrack.utils.compose

import androidx.compose.ui.geometry.Offset
import kotlin.math.atan2

val Offset.vectorAngle: Float get() {
    return atan2(y, x).toDegrees()
}
