package com.gerwalex.example

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.gerwalex.library.animation.HeartBeatAnimation
import kotlin.time.Duration.Companion.milliseconds

@Preview(showBackground = true)
@Composable
fun HeartBeatAnimationPreview() {
    HeartBeatAnimation(
        isVisible = true,
        exitAnimationDuration = 600L.milliseconds,
        onStartExitAnimation = { },
        content = {
            Image(
                painter = painterResource(com.gerwalex.library.R.drawable.demo_image),
                contentDescription = null,
            )
        })
}


