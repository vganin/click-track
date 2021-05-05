package com.vsevolodganin.clicktrack.lib.math

public tailrec fun gcd(lhs: Int, rhs: Int): Int {
    if (rhs == 0) return lhs
    return gcd(rhs, lhs % rhs)
}

public fun gcd(lhs: Rational, rhs: Rational): Rational {
    return gcd(lhs.numerator, rhs.numerator) over lcm(lhs.denominator, rhs.denominator)
}
