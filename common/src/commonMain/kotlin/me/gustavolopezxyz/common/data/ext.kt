/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import me.gustavolopezxyz.db.SelectEntriesFromTransaction


val UnknownAccount = Account(-1, AccountType.Cash, "Unknown", "XXX", 0.0)
val MissingAccount = Account(-1, AccountType.Cash, "Missing account", "XXX", 0.0)

fun Account.getCurrency() = currencyOf(this.currency)

fun SelectEntriesFromTransaction.toEntry() = Entry(
    this.entryId,
    this.transactionId,
    this.accountId,
    this.amount,
    this.incurredAt,
    this.recordedAt
)