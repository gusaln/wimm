/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.core

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.currencyOf
import me.gustavolopezxyz.common.ui.theme.AppColors
import me.gustavolopezxyz.common.ui.theme.AppTheme
import me.gustavolopezxyz.common.ui.theme.NumberTextStyle
import java.math.RoundingMode
import java.text.DecimalFormat


object MoneyTextDefaults {
    const val valueFormatString = "#,###.00"
    val positiveColor = AppColors.positiveMoneyColor
    val negativeColor = AppColors.negativeMoneyColor

    val commonStyle: TextStyle
        @Composable @ReadOnlyComposable get() = TextStyle.Default


    val valueStyle: TextStyle
        @Composable @ReadOnlyComposable get() = NumberTextStyle.copy(fontSize = 1.25.em)


    val currencyStyle: TextStyle
        @Composable @ReadOnlyComposable get() = commonStyle.copy(color = Color.Gray)
}

@Immutable
data class MoneyTextStyle(
    val valueFormatString: String,
    val alwaysShowSign: Boolean,
    val commonStyle: TextStyle,
    val currencyStyle: TextStyle,
    val colorSign: Boolean,
    val valueSpanStyle: SpanStyle,
    val positiveValueColor: Color,
    val negativeValueColor: Color,
) {
    val currencySpanStyle: SpanStyle get() = currencyStyle.copy(textDecoration = TextDecoration.None).toSpanStyle()
    private val positiveSpanStyle: SpanStyle get() = valueSpanStyle.copy(color = positiveValueColor)
    private val negativeSpanStyle: SpanStyle get() = valueSpanStyle.copy(color = negativeValueColor)

    constructor(
        commonStyle: TextStyle,
        valueStyle: TextStyle,
        currencyStyle: TextStyle,
        positiveValueColor: Color,
        negativeValueColor: Color,
        colorSign: Boolean = false,
        alwaysShowSign: Boolean = false,
    ) : this(
        valueFormatString = MoneyTextDefaults.valueFormatString,
        commonStyle = commonStyle,
        currencyStyle = currencyStyle,
        colorSign = colorSign,
        alwaysShowSign = alwaysShowSign,
        valueSpanStyle = valueStyle.toSpanStyle(),
        positiveValueColor = positiveValueColor,
        negativeValueColor = negativeValueColor,
    )

    fun styleOfAmount(amount: Double): SpanStyle {
        if (!colorSign) return valueSpanStyle

        return if (amount < 0) {
            negativeSpanStyle
        } else {
            positiveSpanStyle
        }
    }

    fun colorSign(value: Boolean): MoneyTextStyle {
        return if (value == colorSign) this else copy(colorSign = value)
    }

    fun alwaysShowSign(value: Boolean): MoneyTextStyle {
        return if (value == alwaysShowSign) this else copy(alwaysShowSign = value)
    }

    fun format(format: String? = null): MoneyTextStyle {
        return if (format == null) this else copy(valueFormatString = format)
    }

    fun common(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(commonStyle = style)
    }

    fun value(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(valueSpanStyle = style.toSpanStyle())
    }

    fun currency(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(currencyStyle = style)
    }


    fun positiveColor(color: Color? = null): MoneyTextStyle {
        return if (color == null) this else copy(positiveValueColor = color)
    }


    fun negativeColor(color: Color? = null): MoneyTextStyle {
        return if (color == null) this else copy(negativeValueColor = color)
    }

    companion object {
        val Default: MoneyTextStyle
            @Composable @ReadOnlyComposable get() = MoneyTextStyle(
                MoneyTextDefaults.commonStyle,
                MoneyTextDefaults.valueStyle,
                MoneyTextDefaults.currencyStyle,
                MoneyTextDefaults.positiveColor,
                MoneyTextDefaults.negativeColor,
            )
    }
}

@Composable
fun MoneyText(
    amount: Double,
    currency: Currency,
    modifier: Modifier = Modifier,
    style: MoneyTextStyle = MoneyTextStyle.Default,
) {
    val styleValue by rememberUpdatedState(style)
    val format by derivedStateOf {
        DecimalFormat(styleValue.valueFormatString).apply {
            this.roundingMode = RoundingMode.CEILING
            this.positivePrefix = if (styleValue.alwaysShowSign) "+ " else ""
            this.negativePrefix = "- "
        }
    }

    ProvideTextStyle(style.commonStyle) {
        Text(buildAnnotatedString {
            withStyle(style.currencySpanStyle) {
                append(currency.toString())
            }

            withStyle(style.styleOfAmount(amount)) {
                append("   ")

                append(format.format(amount))
            }
        }, modifier = modifier)
    }
}

@Composable
fun MoneyText(
    amount: Double,
    currency: Currency,
    modifier: Modifier = Modifier,
    colorSign: Boolean = false,
    alwaysShowSign: Boolean = false,
    valueFormat: String? = null,
    positiveColor: Color? = null,
    negativeColor: Color? = null,
    commonStyle: TextStyle? = null,
    valueStyle: TextStyle? = null,
    currencyStyle: TextStyle? = null,
) {
    MoneyText(
        amount,
        currency,
        modifier,
        MoneyTextStyle.Default.colorSign(colorSign).alwaysShowSign(alwaysShowSign).format(valueFormat)
            .common(commonStyle).value(valueStyle).positiveColor(positiveColor).negativeColor(negativeColor)
            .currency(currencyStyle)
    )
}


@Preview
@Composable
fun MoneyTextPreview() {
    val decimal = -(1 shl 15).toDouble() + 0.25
    val notDecimal = 31_415_926.toDouble()

    AppTheme(true) {
        Card(modifier = Modifier.fillMaxSize()) {
            Column {
                Row(modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("Account")

                    Spacer(Modifier.weight(1f))

                    MoneyText(notDecimal, currencyOf("USD"), valueFormat = "#.00")
                }

                Row(modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("Savings")

                    Spacer(Modifier.weight(1f))

                    MoneyText(decimal, currencyOf("USD"), valueFormat = "#,###")
                }

                Row(modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("Body size")

                    Spacer(Modifier.weight(1f))

                    MoneyText(
                        decimal,
                        currencyOf("USD"),
                        commonStyle = MaterialTheme.typography.body2.copy(textDecoration = TextDecoration.LineThrough)
                    )
                }

                Row(modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("Header size")

                    Spacer(Modifier.weight(1f))

                    MoneyText(decimal, currencyOf("USD"), commonStyle = MaterialTheme.typography.h4)
                }
            }
        }
    }
}