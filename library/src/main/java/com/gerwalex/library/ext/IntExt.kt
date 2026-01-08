package com.gerwalex.library.ext

import java.util.Locale

/**
 * Formats an integer with commas as thousands separators.
 * For example, 1234567 becomes "1,234,567".
 * @return A string representation of the integer with thousands separators.
 */
fun Int.formatWithCommas(): String {
    return String.format(Locale.getDefault(), "%,d", this)
}