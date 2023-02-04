/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.theme

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

internal val IbmPlexSansCondensedFontFamily = FontFamily(
    Font(
        resource = "fonts/IBM_Plex_Sans_Condensed/IBMPlexSansCondensed-Light.ttf",
        weight = FontWeight.Light,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/IBM_Plex_Sans_Condensed/IBMPlexSansCondensed-Regular.ttf",
        weight = FontWeight.W400,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/IBM_Plex_Sans_Condensed/IBMPlexSansCondensed-Medium.ttf",
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/IBM_Plex_Sans_Condensed/IBMPlexSansCondensed-SemiBold.ttf",
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/IBM_Plex_Sans_Condensed/IBMPlexSansCondensed-Bold.ttf",
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    )
)

internal val SpectralFontFamily = FontFamily(
    Font(
        resource = "fonts/Spectral/Spectral-Light.ttf",
        weight = FontWeight.Light,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/Spectral/Spectral-Regular.ttf",
        weight = FontWeight.W400,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/Spectral/Spectral-Medium.ttf",
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/Spectral/Spectral-SemiBold.ttf",
        weight = FontWeight.SemiBold,
        style = FontStyle.Normal
    ),
)

fun appTypography(): Typography {
    return Typography(
        defaultFontFamily = IbmPlexSansCondensedFontFamily,
        h1 = TextStyle(
            fontFamily = SpectralFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 74.sp,
        ),
        h2 = TextStyle(
            fontFamily = SpectralFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 66.sp,
        ),
        h3 = TextStyle(
            fontFamily = SpectralFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 58.sp,
        ),
        h4 = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 28.sp,
            letterSpacing = 1.sp,
        ),
        h5 = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 26.sp,
            letterSpacing = 1.sp,
        ),
        h6 = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 22.sp,
            letterSpacing = 1.sp,
        ),
        subtitle1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.125.em,
        ),
        subtitle2 = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 18.sp,
            letterSpacing = 1.25.sp,
        ),
        body1 = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            fontFamily = SpectralFontFamily,
            letterSpacing = 1.sp,
        ),
        body2 = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 1.sp,
        ),
        button = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 0.125.em,
        ),
        caption = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            letterSpacing = 0.1.em,
        ),
        overline = TextStyle(
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp,
        )
    )
}

val Typography.amount
    @Composable @ReadOnlyComposable get() = TextStyle(
        fontFamily = SpectralFontFamily,
        fontWeight = FontWeight.Medium,
    )

val Typography.displayLarge get() = this.h1
val Typography.displayMedium get() = this.h2
val Typography.displaySmall get() = this.h3

val Typography.titleLarge get() = this.h4
val Typography.titleMedium get() = this.h5
val Typography.titleSmall get() = this.h6

val Typography.headlineMedium get() = this.subtitle1
val Typography.headlineSmall get() = this.subtitle2

val Typography.dropdownUnselected
    @Composable get() = this.body2
val Typography.dropdownSelected
    @Composable get() = this.body2.copy(
        color = MaterialTheme.colors.secondary,
        fontWeight = FontWeight.Bold
    )

@Preview
@Composable
fun TypographyDemo() {
    AppTheme(true) {
        Card(modifier = Modifier.fillMaxSize()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "H1 Headline",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.h1
                )
                Text(
                    "H2 Headline",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.h2
                )
                Text(
                    "H3 Headline",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.h3
                )
                Text(
                    "H4 Headline",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.h4
                )
                Text(
                    "H5 Headline",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.h5
                )
                Text(
                    "H6 Headline",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.h6
                )
                Text(
                    "Subtitle 1",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    "Subtitle 2",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.subtitle2
                )
                Row {
                    Text(
                        "Body 1",
                        modifier = Modifier.border(1.dp, Color.DarkGray),
                        style = MaterialTheme.typography.body1
                    )

                    Text(
                        "31,475.92",
                        modifier = Modifier.border(1.dp, Color.DarkGray),
                        style = MaterialTheme.typography.amount
                    )
                }
                Text("Body 2", modifier = Modifier.border(1.dp, Color.DarkGray), style = MaterialTheme.typography.body2)
                Text(
                    "BUTTON",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.button
                )
                Text(
                    "Caption",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.caption
                )
                Text(
                    "OVERLINE",
                    modifier = Modifier.border(1.dp, Color.DarkGray),
                    style = MaterialTheme.typography.overline
                )
            }
        }
    }
}


internal val AppTypography = appTypography()