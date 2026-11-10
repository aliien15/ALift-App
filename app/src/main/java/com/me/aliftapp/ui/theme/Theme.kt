package com.me.aliftapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
    lightColorScheme(
        primary = BackgroundLight, // Buttons and active switch tracks (Basic White)
        onPrimary = TextLight, // Text inside buttons and active switch thumbs (Black)
        surface = HeaderLight, // OutlinedCard background (Light Beige)
        onSurface = TextLight, // Text inside the cards (Black)
        background = BackgroundLight, // Main screen background (Basic White)
        onBackground = TextLight, // Text color on the main screen (Black)
        surfaceVariant = HeaderLight, // Top header box & unchecked switch track (Light Beige)
        onSurfaceVariant = TextLight, // Text color inside the header box (Black)
        outlineVariant = LineLight, // Divider lines and card borders (Light Gray)
        outline = LineLight, // Unchecked switch border (Light Gray to kill the pink tint)
        surfaceTint =
            Color.Transparent, // Prevents Material 3 from adding a default purple shadow tint over
        // cards
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = BackgroundDark, // Buttons and active switch tracks (Dark Gray)
        onPrimary = TextDark, // Text inside buttons and active switch thumbs (White)
        surface = HeaderDark, // OutlinedCard background (Lighter Gray)
        onSurface = TextDark, // Text inside the cards (White)
        background = BackgroundDark, // Main screen background (Dark Gray)
        onBackground = TextDark, // Text color on the main screen (White)
        surfaceVariant = HeaderDark, // Top header box & unchecked switch track (Lighter Gray)
        onSurfaceVariant = TextDark, // Text color inside the header box (White)
        outlineVariant = LineDark, // Divider lines and card borders (Medium-Dark Gray)
        outline = LineDark, // Unchecked switch border (Medium-Dark Gray to kill the pink tint)
        surfaceTint =
            Color.Transparent, // Prevents Material 3 from adding a default purple shadow tint over
        // cards
    )

@Composable
fun ALiftAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> {
                DarkColorScheme
            }

            else -> {
                LightColorScheme
            }
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
