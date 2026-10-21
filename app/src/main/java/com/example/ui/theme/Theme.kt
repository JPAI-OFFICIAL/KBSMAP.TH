package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MapPrimaryDark,
    onPrimary = MapOnPrimaryDark,
    primaryContainer = MapPrimaryContainerDark,
    onPrimaryContainer = MapOnPrimaryContainerDark,
    secondary = MapSecondaryDark,
    onSecondary = MapOnSecondaryDark,
    secondaryContainer = MapSecondaryContainerDark,
    onSecondaryContainer = MapOnSecondaryContainerDark,
    tertiary = MapTertiaryDark,
    onTertiary = MapOnTertiaryDark,
    tertiaryContainer = MapTertiaryContainerDark,
    onTertiaryContainer = MapOnTertiaryContainerDark,
    background = MapBackgroundDark,
    onBackground = MapOnBackgroundDark,
    surface = MapSurfaceDark,
    onSurface = MapOnSurfaceDark,
    surfaceVariant = MapSurfaceVariantDark,
    onSurfaceVariant = MapOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = MapPrimaryLight,
    onPrimary = MapOnPrimaryLight,
    primaryContainer = MapPrimaryContainerLight,
    onPrimaryContainer = MapOnPrimaryContainerLight,
    secondary = MapSecondaryLight,
    onSecondary = MapOnSecondaryLight,
    secondaryContainer = MapSecondaryContainerLight,
    onSecondaryContainer = MapOnSecondaryContainerLight,
    tertiary = MapTertiaryLight,
    onTertiary = MapOnTertiaryLight,
    tertiaryContainer = MapTertiaryContainerLight,
    onTertiaryContainer = MapOnTertiaryContainerLight,
    background = MapBackgroundLight,
    onBackground = MapOnBackgroundLight,
    surface = MapSurfaceLight,
    onSurface = MapOnSurfaceLight,
    surfaceVariant = MapSurfaceVariantLight,
    onSurfaceVariant = MapOnSurfaceVariantLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors for cohesive identity
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
