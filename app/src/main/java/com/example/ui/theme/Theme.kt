package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EasyPcbDarkColorScheme = darkColorScheme(
    primary = PcbGreenPrimary,
    onPrimary = Color(0xFF00391A),
    primaryContainer = Color(0xFF005228),
    onPrimaryContainer = PcbGreenLight,
    
    secondary = CopperGoldLight,
    onSecondary = Color(0xFF422B00),
    secondaryContainer = Color(0xFF5E3F00),
    onSecondaryContainer = Color(0xFFFFDF9E),
    
    tertiary = SiliconTraceBlue,
    onTertiary = Color(0xFF003548),
    tertiaryContainer = Color(0xFF004D68),
    onTertiaryContainer = Color(0xFFBCE9FF),
    
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = NavySurfaceContainer,
    
    outline = GraphiteBorder,
    outlineVariant = GraphiteSubtle,
    error = DrcErrorRed,
    onError = Color.White
)

private val EasyPcbLightColorScheme = lightColorScheme(
    primary = PcbGreenDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4F8E8),
    onPrimaryContainer = Color(0xFF00391A),
    
    secondary = CopperGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF1D6),
    onSecondaryContainer = Color(0xFF422B00),
    
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2FE),
    onTertiaryContainer = Color(0xFF003548),
    
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    
    outline = Color(0xFFCBD5E1),
    error = DrcErrorRed,
    onError = Color.White
)

@Composable
fun EasyPcbTheme(
    darkTheme: Boolean = true, // Default to EDA dark engineering theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EasyPcbDarkColorScheme else EasyPcbLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
