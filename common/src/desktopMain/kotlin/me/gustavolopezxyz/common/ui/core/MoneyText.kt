/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.core

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.currencyOf
import me.gustavolopezxyz.common.ui.theme.AppColors
import me.gustavolopezxyz.common.ui.theme.AppTheme
import me.gustavolopezxyz.common.ui.theme.NumberTextStyle


object MoneyTextDefaults {
    const val valueFormat = "%.2f"
    val positiveColor = AppColors.positiveMoneyColor
    val negativeColor = AppColors.negativeMoneyColor

    val commonStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = TextStyle.Default


    val valueStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = NumberTextStyle.copy(fontSize = 1.25.em)


    val currencyStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = TextStyle.Default.copy(color = Color.Gray)
}

@Immutable
data class MoneyTextStyle(
    val valueFormat: String,
    val commonStyle: TextStyle,
    val currencySpanStyle: SpanStyle,
    val colorSign: Boolean,
    val valueSpanStyle: SpanStyle,
    val positiveValueColor: Color,
    val negativeValueColor: Color,
) {
    val positiveSpanStyle: SpanStyle get() = valueSpanStyle.copy(color = positiveValueColor)
    val negativeSpanStyle: SpanStyle get() = valueSpanStyle.copy(color = negativeValueColor)

    constructor(
        commonStyle: TextStyle,
        valueStyle: TextStyle,
        currencyStyle: TextStyle,
        positiveValueColor: Color,
        negativeValueColor: Color,
        colorSign: Boolean = false,
    ) : this(
        valueFormat = MoneyTextDefaults.valueFormat,
        commonStyle = commonStyle,
        currencySpanStyle = currencyStyle.toSpanStyle(),
        colorSign = colorSign,
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

    fun colorSign(): MoneyTextStyle {
        return if (colorSign) this else copy(colorSign = true)
    }

    fun dontColorSign(): MoneyTextStyle {
        return if (!colorSign) this else copy(colorSign = false)
    }

    fun format(format: String? = null): MoneyTextStyle {
        return if (format == null) this else copy(valueFormat = format)
    }

    fun common(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(commonStyle = style)
    }

    fun value(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(valueSpanStyle = style.toSpanStyle())
    }

    fun currency(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(currencySpanStyle = style.toSpanStyle())
    }


    fun positiveColor(color: Color? = null): MoneyTextStyle {
        return if (color == null) this else copy(positiveValueColor = color)
    }


    fun negativeColor(color: Color? = null): MoneyTextStyle {
        return if (color == null) this else copy(negativeValueColor = color)
    }

    companion object {
        val Default: MoneyTextStyle
            @Composable
            @ReadOnlyComposable
            get() = MoneyTextStyle(
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
    Text(buildAnnotatedString {
        withStyle(
            style.currencySpanStyle
        ) {
            append(currency.toString())
        }

        append("   ")

        withStyle(
            style.styleOfAmount(amount)
        ) { append(style.valueFormat.format(amount)) }
    }, modifier = modifier, style = style.commonStyle)
}

@Composable
fun MoneyText(
    amount: Double,
    currency: Currency,
    modifier: Modifier = Modifier,
    colorSign: Boolean = false,
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
        MoneyTextStyle.Default.colorSign(colorSign).format(valueFormat).common(commonStyle).value(valueStyle)
            .positiveColor(positiveColor)
            .negativeColor(negativeColor).currency(currencyStyle)
    )
}


@Preview
@Composable
fun MoneyTextPreview() {
    AppTheme(true) {
        Card {
            Row(modifier = Modifier.padding(32.dp).fillMaxSize()) {
                Text("Account")

                Spacer(Modifier.weight(1f))

                MoneyText(100.0, currencyOf("USD"))
            }
        }
    }
}