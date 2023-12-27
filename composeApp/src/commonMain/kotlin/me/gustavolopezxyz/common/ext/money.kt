/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ext

import me.gustavolopezxyz.common.money.Currency
import me.gustavolopezxyz.common.money.Money
import me.gustavolopezxyz.common.money.currencyOf

fun String.toCurrency() = currencyOf(this)

fun Number.toMoney(currency: Currency) = Money(currency, this.toDouble())

fun Number.toMoney(currencyCode: String): Money = this.toMoney(currencyCode.toCurrency())
