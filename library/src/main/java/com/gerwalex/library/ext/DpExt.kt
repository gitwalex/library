package com.gerwalex.library.ext

import android.content.res.Resources
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

val Dp.toPx: Float
    get() =
        (this * Resources.getSystem().displayMetrics.density).value

