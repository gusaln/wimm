/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import me.gustavolopezxyz.db.Account
import me.gustavolopezxyz.db.Entry

fun Account.getBalance() = Money(this.balance_currency, this.balance_value)
fun Account.getInitialBalance() = Money(this.balance_currency, this.initial_value)
fun Account.getCurrency() = currencyOf(this.balance_currency)


fun Entry.getBalance() = Money(this.amount_currency, this.amount_value)
fun Entry.getCurrency() = currencyOf(this.amount_currency)