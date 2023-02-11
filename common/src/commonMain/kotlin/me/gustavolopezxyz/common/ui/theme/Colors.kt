/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

internal val green400 = Color(0xFF66BB6A)
internal val red400 = Color(0xFFEF5350)

@Immutable
object AppColors {
    val positiveMoney: Color = green400
    val negativeMoney: Color = red400
    val cardBackground: Color = Color(0xFF1E1E1E)
}