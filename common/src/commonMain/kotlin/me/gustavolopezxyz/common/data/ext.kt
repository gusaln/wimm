/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import me.gustavolopezxyz.db.SelectEntriesFromRecord


val UnknownAccount = Account(-1, "Unknown", "XXX", 0.0, 0.0)
val MissingAccount = Account(-1, "Missing account", "XXX", 0.0, 0.0)

fun Account.getBalance() = Money(this.balance_currency, this.balance_value)
fun Account.getInitialBalance() = Money(this.balance_currency, this.initial_value)
fun Account.getCurrency() = currencyOf(this.balance_currency)

fun Entry.getAmount() = Money(this.amount_currency, this.amount_value)

fun SelectEntriesFromRecord.toEntry() = Entry(
    this.id,
    this.account_id,
    this.record_id,
    this.description,
    this.amount_currency,
    this.amount_value,
    this.incurred_at,
    this.recorded_at
)