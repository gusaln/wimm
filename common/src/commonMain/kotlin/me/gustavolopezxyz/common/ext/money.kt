/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ext

import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.Money
import me.gustavolopezxyz.common.data.currencyOf

fun String.currency() = currencyOf(this)

fun Number.money(currency: Currency) = Money(currency, this.toDouble())

fun Number.money(currencyCode: String): Money = this.money(currencyCode.currency())
