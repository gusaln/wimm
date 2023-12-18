/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


val AppLightColorScheme = lightColorScheme(
    // M3 light Color parameters
)
val AppDarkColorScheme = darkColorScheme(
    primary = Color(0x5D, 0x89, 0xC6),
    secondary = Color(0x8E, 0xC7, 0xD2),
    tertiary = Color(0xD7, 0x59, 0x55),
    surfaceTint = Color(0x45, 0x45, 0x45),
)

val AppShapes = Shapes(
    small = CutCornerShape(topStart = 8.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(8.dp)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    AppTheme(false, content)
}

@Composable
actual fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) AppDarkColorScheme else AppLightColorScheme,
        typography = AppTypography,
        shapes = AppShapes
    ) {
        content()
    }
}
