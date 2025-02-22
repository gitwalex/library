package com.gerwalex.library.ext

import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit


@Composable
fun Dp.textSp() = with(LocalDensity.current) {
    this@textSp.toSp()
}

val Dp.textSp: TextUnit
    @Composable get() = with(LocalDensity.current) {
        this@textSp.toSp()
    }

/**
 * A convenience property to access the scaled density of the display.
 *
 * This property provides a consistent way to obtain the scaled density, handling
 * API differences between Android versions.
 *
 * - On Android 14 (API level 34) and above, it calculates the density-independent
 *   pixel (dp) value for 1dp using `TypedValue.applyDimension` with `COMPLEX_UNIT_DIP`.
 *   This approach is recommended for these newer API levels.
 * - On Android versions prior to 14, it directly returns the deprecated `scaledDensity`
 *   value. Although deprecated, it's still functional on these older versions.
 *
 * The scaled density represents the scaling factor for fonts and other elements that
 * are supposed to be scaled according to user preferences (like font size).
 * It's often used to convert dimension values from density-independent pixels (dp)
 * to pixels (px) in a way that respects the user's scaling settings.
 *
 * Example of usage:
 *
 * ```kotlin
 * val displayMetrics = resources.displayMetrics
 * val scaledDensity = displayMetrics.densityScaled
 * val textSizeInPx = 16f * scaledDensity // Convert 16dp to pixels, respecting scaling
 * ```
 */
val DisplayMetrics.densityScaled: Float
    get() {
        return if (Build.VERSION.SDK_INT >= 34)
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, this)
        else @Suppress("DEPRECATION") this.scaledDensity
    }

