/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.money

import androidx.compose.runtime.Immutable
import me.gustavolopezxyz.common.ext.toCurrency
import java.math.RoundingMode
import java.text.DecimalFormat

val MoneyAmountFormat = DecimalFormat("#,##0.00").apply {
    roundingMode = RoundingMode.CEILING
    positivePrefix = " "
    negativePrefix = " "
}

@Immutable
data class Money(val currency: Currency, val value: Double) {
    fun withValue(value: Number): Money = withValue(value.toDouble())

    fun withValue(value: Double): Money = this.copy(value = value)

    fun withCurrency(currencyCode: String): Money = withCurrency(currencyCode.toCurrency())

    fun withCurrency(currency: Currency): Money = this.copy(currency = currency)

    operator fun plus(other: Money): Money {
        if (other.currency != this.currency) {
            throw IllegalArgumentException("Cannot add ${this.currency} to ${other.currency}")
        }

        return plus(other.value)
    }

    operator fun plus(other: Number): Money = plus(other.toDouble())

    operator fun plus(other: Double): Money = this.copy(currency = this.currency, value = this.value + other)

    operator fun minus(other: Money): Money {
        if (other.currency != this.currency) {
            throw IllegalArgumentException("Cannot subtract ${other.currency} from ${this.currency}")
        }

        return minus(other.value)
    }

    operator fun minus(other: Number): Money = minus(other.toDouble())

    operator fun minus(other: Double): Money = this.copy(currency = this.currency, value = this.value - other)

    operator fun compareTo(other: Money): Int {
        if (other.currency != this.currency) {
            throw IllegalArgumentException("Cannot compare ${this.currency} to ${other.currency}")
        }

        return compareTo(other.value)
    }

    operator fun compareTo(other: Number): Int = compareTo(other.toDouble())

    operator fun compareTo(other: Double): Int = this.value.compareTo(other)

    override fun toString(): String {
        if (this.value < 0) {
            return "- ${currency.symbol} ${MoneyAmountFormat.format(this.value)}"
        }
        return "${currency.symbol} ${MoneyAmountFormat.format(this.value)}"
    }
}

