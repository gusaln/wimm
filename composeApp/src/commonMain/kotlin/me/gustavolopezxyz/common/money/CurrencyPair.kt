/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.money

import androidx.compose.runtime.Immutable

@Immutable
data class CurrencyPair(val baseCurrency: Currency, val counterCurrency: Currency, val value: Double) {
    override fun toString(): String {
        return "$baseCurrency/$counterCurrency ${MoneyAmountFormat.format(value)}"
    }

    fun convert(amount: Money): Money {
        if (baseCurrency != amount.currency) {
            throw IllegalArgumentException("Pair $this can't convert ${amount.currency}")
        }

        return amount.copy(currency = counterCurrency, value = amount.value * value)
    }
}