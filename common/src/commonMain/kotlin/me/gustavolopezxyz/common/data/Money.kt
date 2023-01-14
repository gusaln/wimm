/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import me.gustavolopezxyz.common.ext.currency

data class Currency constructor(val code: String) {
    override fun toString(): String {
        return code
    }
}

private val currencyRepository: HashMap<String, Currency> = hashMapOf()

fun currencyOf(code: String): Currency {
    val c = code.uppercase().trim()

    return currencyRepository.getOrPut(c) { Currency(c) }
}

data class Money(val currency: Currency, val value: Double) {
    constructor(currencyCode: String, value: Double) : this(currencyCode.currency(), value)

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

    fun isNegative(): Boolean = this.value < 0

    fun isPositive(): Boolean = this.value > 0
}

