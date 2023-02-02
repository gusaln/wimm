/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

internal val ibmPlexMono = FontFamily(
    Font(
        resource = "fonts/IBM_Plex_Mono/IBMPlexMono-Light.ttf",
        weight = FontWeight.Light,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/IBM_Plex_Mono/IBMPlexMono-Regular.ttf",
        weight = FontWeight.W400,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/IBM_Plex_Mono/IBMPlexMono-Medium.ttf",
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/IBM_Plex_Mono/IBMPlexMono-SemiBold.ttf",
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal
    )
)

internal val lora = FontFamily(
    Font(
        resource = "fonts/Lora/Lora-Regular.ttf",
        weight = FontWeight.W400,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/Lora/Lora-Medium.ttf",
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/Lora/Lora-SemiBold.ttf",
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal
    ),
)

val NumberTextStyle = TextStyle(fontFamily = lora, fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp)

fun appTypography(): Typography {
    return Typography(defaultFontFamily = ibmPlexMono)
}

internal val AppTypography = appTypography()