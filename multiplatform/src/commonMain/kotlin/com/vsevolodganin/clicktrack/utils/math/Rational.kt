package com.vsevolodganin.clicktrack.utils.math

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration

@Serializable
data class Rational(
    private val numerator_: Int,
    private val denominator_: Int,
) {
    @Transient
    private val gcd: Int = gcd(numerator_, denominator_)

    @Transient
    val numerator: Int = numerator_ / gcd

    @Transient
    val denominator: Int = denominator_ / gcd

    override fun toString(): String {
        return "$numerator over $denominator"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Rational

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numerator
        result = 31 * result + denominator
        return result
    }
}

infix fun Int.over(denominator: Int): Rational = Rational(this, denominator)

fun Int.toRational(): Rational = this over 1

private val ZERO_: Rational = 0.toRational()
val Rational.Companion.ZERO: Rational get() = ZERO_

operator fun Rational.compareTo(value: Rational): Int {
    val n1 = n * value.d
    val n2 = value.n * d
    return n1.compareTo(n2)
}

operator fun Rational.plus(value: Rational): Rational {
    return Rational(n * value.d + value.n * d, d * value.d)
}

operator fun Rational.minus(value: Rational): Rational {
    return Rational(n * value.d - value.n * d, d * value.d)
}

operator fun Rational.times(value: Rational): Rational {
    return Rational(n * value.n, d * value.d)
}

operator fun Rational.times(value: Duration): Duration {
    return value * n / d
}

operator fun Duration.times(value: Rational): Duration {
    return value * this
}

operator fun Rational.times(value: Int): Int {
    return value * n / d
}

operator fun Int.times(value: Rational): Int {
    return value * this
}

operator fun Rational.div(value: Duration): Duration {
    return value * d / n
}

operator fun Duration.div(value: Rational): Duration {
    return value / this
}

operator fun Rational.div(value: Int): Int {
    return value * d / n
}

operator fun Int.div(value: Rational): Int {
    return value / this
}

fun min(lhs: Rational, rhs: Rational): Rational {
    return if (lhs < rhs) lhs else rhs
}

private val Rational.n get() = numerator
private val Rational.d get() = denominator
