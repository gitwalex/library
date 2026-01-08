package com.gerwalex.library.ext

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt


/**
 * Formats a Double as a price string.
 *
 * This extension function takes a Double value and formats it into a string
 * representation of a price, with two decimal places.
 *
 * @param currencySymbol The currency symbol to prepend to the formatted number.
 *                       If not provided, it attempts to get the symbol from the default Locale.
 *                       However, the default parameter `Locale.getDefault().` is incomplete in the original code
 *                       and should be `Locale.getDefault().currency.symbol`.
 *                       A safer default might be just an empty string or a specific symbol like "$".
 * @return A string representing the price, e.g., "$123.45".
 */
fun Double.formatAsPrice(): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val currencySymbol = numberFormat.currency?.symbol
    return "$currencySymbol%.2f".format(this)
}

/**
 * Rounds a Float to a specified number of decimal places.
 *
 * This extension function takes a Float and rounds it to the number of decimal
 * places indicated by the `decimals` parameter.
 *
 * @param decimals The number of decimal places to round to. Must be a non-negative integer.
 * @return The rounded Float value. For example, `1.2345f.roundTo(2)` would return `1.23f`.
 */
fun Double.roundTo(decimals: Int): Double {
    val multiplier = 10.0.pow(decimals)
    return (this * multiplier).roundToInt() / multiplier
}