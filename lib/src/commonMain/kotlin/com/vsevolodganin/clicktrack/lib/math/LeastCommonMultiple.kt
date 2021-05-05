package com.vsevolodganin.clicktrack.lib.math

public fun lcm(lhs: Int, rhs: Int): Int {
    return lhs * rhs / gcd(lhs, rhs)
}

public fun lcm(lhs: Rational, rhs: Rational): Rational {
    return lcm(lhs.numerator, rhs.numerator) over gcd(lhs.denominator, rhs.denominator)
}
