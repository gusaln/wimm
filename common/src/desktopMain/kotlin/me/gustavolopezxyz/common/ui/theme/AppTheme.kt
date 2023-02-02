/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val LightColors = lightColors()
val DarkColors = darkColors().copy(error = red400)

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
        colors = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes
    ) {
        content()
    }
}
