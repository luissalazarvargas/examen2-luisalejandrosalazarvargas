package com.examen.paniniticke.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PaniniOrangeLight,
    onPrimary = SurfaceDark,
    primaryContainer = PaniniOrangeDark,
    onPrimaryContainer = PaniniOrangeContainer,
    secondary = FifaBlueLight,
    onSecondary = SurfaceDark,
    secondaryContainer = FifaBlueDark,
    onSecondaryContainer = FifaBlueContainer,
    background = SurfaceDark,
    onBackground = White,
    surface = SurfaceContainerDark,
    onSurface = White,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = White,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PaniniOrange,
    onPrimary = White,
    primaryContainer = PaniniOrangeContainer,
    onPrimaryContainer = OnPaniniOrangeContainer,
    secondary = FifaBlue,
    onSecondary = White,
    secondaryContainer = FifaBlueContainer,
    onSecondaryContainer = OnFifaBlueContainer,
    background = LightGray,
    onBackground = NeutralGray,
    surface = White,
    onSurface = NeutralGray,
    surfaceVariant = LightGray,
    onSurfaceVariant = NeutralGray,
    outline = NeutralGray

    /* Otras opciones a sobreescribir si se requiere:
    tertiary = Pink40,
    error = Red40
    */
)

@Composable
fun PaniniTicketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color es válido a partir de Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PaniniTypography,
        content = content
    )
}