/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.core

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import me.gustavolopezxyz.common.data.Currency


object MoneyTextDefaults {
    const val valueFormat = "%.2f"
    val positiveColor = Color.Unspecified
    val negativeColor = Color.Red

    val commonStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = TextStyle.Default


    val valueStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = TextStyle.Default.copy(fontSize = 1.3.em)


    val currencyStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = TextStyle.Default.copy(
            color = Color.Gray,
            fontSize = MaterialTheme.typography.caption.fontSize,
            baselineShift = BaselineShift.Subscript
        )
}

data class MoneyTextStyle(
    val valueFormat: String,
    val commonStyle: TextStyle,
    val currencySpanStyle: SpanStyle,
    val positiveSpanStyle: SpanStyle,
    val negativeSpanStyle: SpanStyle,
) {
    constructor(
        commonStyle: TextStyle,
        valueStyle: TextStyle,
        currencyStyle: TextStyle,
        positiveValueColor: Color,
        negativeValueColor: Color,
    ) : this(
        MoneyTextDefaults.valueFormat,
        commonStyle,
        currencyStyle.toSpanStyle(),
        valueStyle.copy(color = positiveValueColor).toSpanStyle(),
        valueStyle.copy(color = negativeValueColor).toSpanStyle(),
    )

    fun styleOfAmount(amount: Double): SpanStyle {
        return if (amount < 0) {
            negativeSpanStyle
        } else {
            positiveSpanStyle
        }
    }

    fun format(format: String? = null): MoneyTextStyle {
        return if (format == null) this else copy(valueFormat = format)
    }

    fun common(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(commonStyle = style)
    }

    fun value(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(
            positiveSpanStyle = style.copy(color = this.positiveSpanStyle.color).toSpanStyle(),
            negativeSpanStyle = style.copy(color = this.negativeSpanStyle.color).toSpanStyle(),
        )
    }

    fun currency(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(currencySpanStyle = style.toSpanStyle())
    }


    fun positiveColor(color: Color? = null): MoneyTextStyle {
        return if (color == null) this else copy(positiveSpanStyle = positiveSpanStyle.copy(color))
    }


    fun negativeColor(color: Color? = null): MoneyTextStyle {
        return if (color == null) this else copy(negativeSpanStyle = negativeSpanStyle.copy(color))

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
            style.styleOfAmount(amount)
        ) { append(style.valueFormat.format(amount)) }

        append(' ')

        withStyle(
            style.currencySpanStyle
        ) {
            append(
                currency.toString()
            )
        }
    }, modifier = modifier, style = style.commonStyle)
}

@Composable
fun MoneyText(
    amount: Double,
    currency: Currency,
    modifier: Modifier = Modifier,
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
        MoneyTextStyle.Default.format(valueFormat).common(commonStyle).value(valueStyle).positiveColor(positiveColor)
            .negativeColor(negativeColor).currency(currencyStyle)
    )
}