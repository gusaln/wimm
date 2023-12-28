/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.money

import androidx.compose.runtime.Immutable

@Immutable
data class Currency internal constructor(val code: String, val symbol: String) {
    override fun toString(): String {
        return code
    }
}

val MissingCurrency = Currency("XXX", "?")
private val currencyRepository: Map<String, Currency> = listOf(
    Currency("USD", "$"),
    Currency("VED", "Bs")
).associateBy { it.code }

fun currencyOf(code: String): Currency {
    return currencyRepository.getOrDefault(code.uppercase().trim(), MissingCurrency)
}