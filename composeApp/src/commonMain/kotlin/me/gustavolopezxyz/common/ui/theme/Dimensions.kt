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
    val spacing: Dimensions = Dimensions(
        Spacing.SPACE_100.dp,
        Spacing.SPACE_200.dp,
        Spacing.SPACE_300.dp,
        Spacing.SPACE_400.dp
    ),
    val padding: Dimensions = Dimensions(Spacing.SPACE_50.dp, Spacing.SPACE_150.dp, Spacing.SPACE_250.dp, Spacing.SPACE_350.dp),
) {
    companion object {
        val Default = AppDimensions()
    }
}

data object Spacing {
    //  small
    const val SPACE_50 = (8 * 50 / 100)
    const val SPACE_100 = (8 * 100 / 100)

    //  medium
    const val SPACE_150 = (8 * 150 / 100)
    const val SPACE_200 = (8 * 200 / 100)
    const val SPACE_250 = (8 * 250 / 100)
    const val SPACE_300 = (8 * 300 / 100)
    const val SPACE_350 = (8 * 350 / 100)

    //  large
    const val SPACE_400 = (8 * 400 / 100)
    const val SPACE_500 = (8 * 500 / 100)
    const val SPACE_600 = (8 * 600 / 100)
    const val SPACE_800 = (8 * 800 / 100)
    const val SPACE_1000 = (8 * 1000 / 100)
}
