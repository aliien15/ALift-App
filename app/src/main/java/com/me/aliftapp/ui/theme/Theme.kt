package com.me.aliftapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
    lightColorScheme(
        background = BackgroundLight, // Main screen background (Basic White)
        surfaceVariant = HeaderLight, // Top header box background (Light Beige)
        onBackground = TextLight, // Text color on the main screen (Black)
        onSurfaceVariant = TextLight, // Text color inside the header box (Black)
        outlineVariant = LineLight, // Divider lines and card borders (Light Gray)
    )

private val DarkColorScheme =
    darkColorScheme(
        background = BackgroundDark, // Main screen background (Dark Gray)
        surfaceVariant = HeaderDark, // Top header box background (Lighter Gray)
        onBackground = TextDark, // Text color on the main screen (White)
        onSurfaceVariant = TextDark, // Text color inside the header box (White)
        outlineVariant = LineDark, // Divider lines and card borders (Medium-Dark Gray)
    )

@Composable
fun ALiftAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
