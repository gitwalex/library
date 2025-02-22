package com.gerwalex.library.composables

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat


@Immutable
class MyColors(
    internal
    val material: ColorScheme,
)


internal lateinit var themeColors: MyColors

/**
 * AppTheme mit CustomColors
 * https://gustav-karlsson.medium.com/extending-the-jetpack-compose-material-theme-with-more-colors-e1b849390d50
 */
@Composable
internal fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isInDarkMode by remember(key1 = useDarkTheme) { mutableStateOf(useDarkTheme) }
    LaunchedEffect(key1 = useDarkTheme) {
        val nightMode = if (isInDarkMode)
            AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            with(view.context) {
                val window = (this as Activity).window
                val insets = WindowCompat.getInsetsController(window, view)
                window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
                window.navigationBarColor =
                    ContextCompat.getColor(this, android.R.color.transparent)
                insets.isAppearanceLightStatusBars = !isInDarkMode
                insets.isAppearanceLightNavigationBars = !isInDarkMode
            }
        }
    }
    val lightColorPalette = MyColors(
        material = MaterialTheme.colorScheme,
    )
    val darkColorPalette = MyColors(
        material = MaterialTheme.colorScheme,
    )
    AnimatedContent(targetState = isInDarkMode, label = "animate Thema",
        transitionSpec = {
            fadeIn(
                tween(1_000, easing = LinearEasing)

            ).togetherWith(
                fadeOut(
                    tween(1000, easing = LinearEasing)
                ) //+ scaleOut(tween(500))

            )

        }) { darkMode ->
        themeColors = if (!darkMode) {
            lightColorPalette
        } else {
            darkColorPalette
        }
        val localAppColors = staticCompositionLocalOf { themeColors }

        CompositionLocalProvider(
            localAppColors provides themeColors,
        ) {
            MaterialTheme(
                colorScheme = themeColors.material,
                typography = typography,
                content = content
            )
        }
    }
}