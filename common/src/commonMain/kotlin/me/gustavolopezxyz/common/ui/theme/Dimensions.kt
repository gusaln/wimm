/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp,
) {
    constructor(small: Dp, medium: Dp, large: Dp) : this(small, medium, large, large)
    constructor(small: Dp, medium: Dp) : this(small, medium, medium)
    constructor(small: Dp) : this(small, small)
}

data class AppDimensions(
    val spacing: Dimensions = Dimensions(8.dp, 16.dp, 24.dp),
    val padding: Dimensions = Dimensions(4.dp, 8.dp, 16.dp),
    val fieldSpacing: Dp = 12.dp,
    val topBarHorizontalPadding: Dp = 24.dp,
) {
    companion object {
        val Default = AppDimensions()
    }
}