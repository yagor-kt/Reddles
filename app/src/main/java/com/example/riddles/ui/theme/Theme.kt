package com.example.riddles.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val RiddlesLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = MediumColor,
    onSecondary = OnPrimary,
    tertiary = HardColor,
    onTertiary = OnPrimary,
    error = WrongRed,
    onError = OnPrimary,
    background = LightBackground,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark
)

@Composable
fun RiddlesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RiddlesLightColorScheme,
        typography = Typography,
        content = content
    )
}