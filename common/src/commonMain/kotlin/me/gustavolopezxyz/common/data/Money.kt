/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import androidx.compose.runtime.Immutable
import me.gustavolopezxyz.common.ext.toCurrency

@Immutable
data class Currency internal constructor(val code: String, val symbol: String) {
    override fun toString(): String {
        return code
    }
}

val missingCurrency = Currency("XXX", "?")

private val currencyRepository: HashMap<String, Currency> = hashMapOf(
    Pair("USD", Currency("USD", "$"))
)

fun currencyOf(code: String): Currency {
    return currencyRepository.getOrDefault(code.uppercase().trim(), missingCurrency)
}

@Immutable
data class Money(val currency: Currency, val value: Double) {
    constructor(currencyCode: String, value: Double) : this(currencyCode.toCurrency(), value)

    operator fun plus(other: Money): Money {
        return plus(other.value)
    }

    operator fun plus(other: Number): Money = plus(other.toDouble())

    operator fun plus(other: Double): Money = this.copy(currency = this.currency, value = this.value + other)

    operator fun minus(other: Money): Money {
        return minus(other.value)
    }

    operator fun minus(other: Number): Money = minus(other.toDouble())

    operator fun minus(other: Double): Money = this.copy(currency = this.currency, value = this.value - other)

    operator fun compareTo(other: Money): Int = compareTo(other.value)

    operator fun compareTo(other: Number): Int = compareTo(other.toDouble())

    operator fun compareTo(other: Double): Int = this.value.compareTo(other)
}

