package com.vsevolodganin.clicktrack.utils.math

tailrec fun gcd(
    lhs: Int,
    rhs: Int,
): Int {
    if (rhs == 0) return lhs
    return gcd(rhs, lhs % rhs)
}

fun gcd(
    lhs: Rational,
    rhs: Rational,
): Rational {
    return gcd(lhs.numerator, rhs.numerator) over lcm(lhs.denominator, rhs.denominator)
}
