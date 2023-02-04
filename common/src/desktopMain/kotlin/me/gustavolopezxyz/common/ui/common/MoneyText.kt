/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.currencyOf
import me.gustavolopezxyz.common.ui.theme.AppTheme
import me.gustavolopezxyz.common.ui.theme.amount
import java.math.RoundingMode
import java.text.DecimalFormat


object MoneyTextDefaults {
    val commonStyle: TextStyle
        @Composable @ReadOnlyComposable get() = LocalTextStyle.current


    val valueStyle: TextStyle
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography.amount


    val currencyStyle: TextStyle
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography.amount.copy(color = Color.Gray)
}

@Immutable
data class MoneyTextStyle(
    val commonStyle: TextStyle,
    val valueStyleBase: TextStyle,
    val currencyStyleBase: TextStyle,
) {
    val currencyStyle: TextStyle get() = commonStyle + currencyStyleBase
    val valueStyle: TextStyle get() = commonStyle + valueStyleBase

    fun common(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(commonStyle = style)
    }

    fun value(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(valueStyleBase = style)
    }

    fun currency(style: TextStyle? = null): MoneyTextStyle {
        return if (style == null) this else copy(currencyStyleBase = style)
    }

    companion object {
        val Default: MoneyTextStyle
            @Composable @ReadOnlyComposable get() = MoneyTextStyle(
                MoneyTextDefaults.commonStyle,
                MoneyTextDefaults.valueStyle,
                MoneyTextDefaults.currencyStyle,
            )
    }
}

internal val MoneyTextAmountFormat = DecimalFormat("#,##0.00").apply {
    roundingMode = RoundingMode.CEILING
    positivePrefix = " "
    negativePrefix = " "
}

internal fun formatMoneyTextAmount(amount: Number): String = MoneyTextAmountFormat.format(amount)

@Composable
fun MoneyText(
    amount: Double,
    currency: Currency,
    modifier: Modifier = Modifier,
    style: MoneyTextStyle = MoneyTextStyle.Default,
) {
    val symbol = if (amount < 0) "- ${currency.symbol}" else currency.symbol

    ProvideTextStyle(style.commonStyle) {
        Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(symbol, style = style.currencyStyle)

            Text(formatMoneyTextAmount(amount), style = style.valueStyle)
        }
    }
}

@Composable
fun MoneyText(
    amount: Double,
    currency: Currency,
    modifier: Modifier = Modifier,
    commonStyle: TextStyle? = null,
    valueStyle: TextStyle? = null,
    currencyStyle: TextStyle? = null,
) {
    MoneyText(
        amount,
        currency,
        modifier,
        MoneyTextStyle.Default
            .common(commonStyle).value(valueStyle)
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
                    Text("Account", Modifier.weight(1f))

                    Spacer(Modifier.weight(1f))

                    MoneyText(notDecimal, currencyOf("USD"), modifier = Modifier.weight(1f))
                }

                Row(modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("Savings")

                    Spacer(Modifier.weight(1f))

                    MoneyText(decimal, currencyOf("USD"))
                }

                Row(modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("Common decorations")

                    Spacer(Modifier.weight(1f))

                    MoneyText(
                        decimal,
                        currencyOf("USD"),
                        commonStyle = LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough)
                    )
                }

                Row(modifier = Modifier.padding(32.dp).fillMaxWidth()) {
                    Text("Common size")

                    Spacer(Modifier.weight(1f))

                    MoneyText(decimal, currencyOf("USD"), commonStyle = MaterialTheme.typography.h4)
                }
            }
        }
    }
}