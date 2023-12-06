/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.theme

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
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
import androidx.compose.ui.unit.sp

internal val IbmPlexSansCondensedFontFamily = FontFamily(
    Font(
        resource = "fonts/IBM_Plex_Sans_Condensed/IBMPlexSansCondensed-Light.ttf",
        weight = FontWeight.Light,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/IBM_Plex_Sans_Condensed/IBMPlexSansCondensed-Regular.ttf",
        weight = FontWeight.Normal,
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

internal val LoraFontFamily = FontFamily(
    Font(
        resource = "fonts/Lora/Lora-Light.ttf",
        weight = FontWeight.Light,
        style = FontStyle.Normal
    ),
    Font(
        resource = "fonts/Lora/Lora-Regular.ttf",
        weight = FontWeight.Normal,
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

fun appTypography(): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = LoraFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 46.sp,
            lineHeight = 50.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = LoraFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 42.sp,
            lineHeight = 46.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = LoraFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 38.sp,
            lineHeight = 42.sp,
            letterSpacing = 0.3.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 34.sp,
            letterSpacing = 1.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
//            fontWeight = FontWeight.Light,
            fontSize = 32.sp,
            lineHeight = 36.sp,
            letterSpacing = 1.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
//            fontWeight = FontWeight.Light,
            fontSize = 30.sp,
            letterSpacing = 0.8.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 26.sp,
            lineHeight = 26.sp,
            letterSpacing = 1.3.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
//            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 26.sp,
            letterSpacing = 1.3.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 22.sp,
            letterSpacing = 1.3.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            letterSpacing = 1.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 18.sp,
            lineHeight = 20.sp,
            letterSpacing = 1.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 1.2.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
//            letterSpacing = 0.125.em,
            letterSpacing = 1.1.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 0.4.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = IbmPlexSansCondensedFontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 15.sp,
            letterSpacing = 0.5.sp,
        )
    )
}

val Typography.amount
    @Composable @ReadOnlyComposable get() = TextStyle(
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.Medium,
//        fontSize = 1.15.em,
//        lineHeight = 1.2.em,
        letterSpacing = 0.4.sp,
    )


val Typography.dropdownUnselected
    @Composable get() = this.bodyMedium
val Typography.dropdownSelected
    @Composable get() = this.bodyMedium.copy(
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Bold
    )

@Preview
@Composable
fun TypographyDemo() {
    AppTheme(true) {
        Card(modifier = Modifier.fillMaxSize()) {
            val scroll = rememberScrollState()

            Column(
                modifier = Modifier.padding(4.dp).verticalScroll(scroll),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text("Display large", style = TextStyle.Default)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text("Display medium", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text("Display small", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text("Headline large", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text("Headline medium", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text("Headline small", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text("Title large", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Title medium", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text("Title small", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text("Body Large", style = TextStyle.Default)
                }


                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text("Body medium", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("Body small", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text("Label large", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text("Label medium", style = TextStyle.Default)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text("Label small", style = TextStyle.Default)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This is a word. WOOOOORDS ::: 31,475.92 $",
                        modifier = Modifier.border(2.dp, Color.DarkGray),
                        style = MaterialTheme.typography.amount
                    )
                    Text("Amount 31,475.92 $", style = TextStyle.Default)
                }
            }
        }
    }
}


internal val AppTypography = appTypography()