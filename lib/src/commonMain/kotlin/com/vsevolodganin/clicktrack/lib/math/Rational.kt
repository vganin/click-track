package com.vsevolodganin.clicktrack.lib.math

import com.vsevolodganin.clicktrack.lib.android.AndroidIgnoredOnParcel
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import kotlin.time.Duration
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@AndroidParcelize
public data class Rational(
    private val numerator_: Int,
    private val denominator_: Int,
) : AndroidParcelable {

    @Transient
    @AndroidIgnoredOnParcel
    private val gcd: Int = gcd(numerator_, denominator_)

    @Transient
    @AndroidIgnoredOnParcel
    public val numerator: Int = numerator_ / gcd

    @Transient
    @AndroidIgnoredOnParcel
    public val denominator: Int = denominator_ / gcd

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

public infix fun Int.over(denominator: Int): Rational = Rational(this, denominator)

private val ZERO_: Rational = 0 over 1
public val Rational.Companion.ZERO: Rational get() = ZERO_

public operator fun Rational.compareTo(value: Rational): Int {
    val n1 = n * value.d
    val n2 = value.n * d
    return n1.compareTo(n2)
}

public operator fun Rational.plus(value: Rational): Rational {
    return Rational(n * value.d + value.n * d, d * value.d)
}

public operator fun Rational.minus(value: Rational): Rational {
    return Rational(n * value.d - value.n * d, d * value.d)
}

public operator fun Rational.times(value: Rational): Rational {
    return Rational(n * value.n, d * value.d)
}

public operator fun Rational.times(value: Duration): Duration {
    return value * n / d
}

public operator fun Duration.times(value: Rational): Duration {
    return value * this
}

public operator fun Rational.times(value: Int): Int {
    return value * n / d
}

public operator fun Int.times(value: Rational): Int {
    return value * this
}

public operator fun Rational.div(value: Duration): Duration {
    return value * d / n
}

public operator fun Duration.div(value: Rational): Duration {
    return value / this
}

public operator fun Rational.div(value: Int): Int {
    return value * d / n
}

public operator fun Int.div(value: Rational): Int {
    return value / this
}

public fun min(lhs: Rational, rhs: Rational): Rational {
    return if (lhs < rhs) lhs else rhs
}

private val Rational.n get() = numerator
private val Rational.d get() = denominator
