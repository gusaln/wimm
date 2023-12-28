/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import me.gustavolopezxyz.common.db.SelectAllCategories
import me.gustavolopezxyz.common.db.SelectEntriesForAccount
import me.gustavolopezxyz.common.db.SelectEntriesForTransaction
import me.gustavolopezxyz.common.db.SelectTransactionsInCategoryInRange
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.money.CurrencyPair
import me.gustavolopezxyz.common.money.currencyOf


val UnknownAccount = Account(-1, AccountType.Cash, "Unknown", "XXX", 0.0)
val MissingAccount = Account(-1, AccountType.Cash, "Missing account", "XXX", 0.0)
val MissingCategory = Category(-1, null, "missing", true)

fun Account.getCurrency() = currencyOf(this.currency)

fun Category.toDto(parentCategoryName: String? = null) = CategoryWithParent(
    this.categoryId,
    this.parentCategoryId,
    parentCategoryName,
    this.name,
    this.isLocked
)

fun SelectAllCategories.toDto() = CategoryWithParent(
    this.categoryId,
    this.parentCategoryId,
    this.parentCategoryName,
    this.name,
    this.isLocked
)

fun SelectEntriesForTransaction.toEntry() = Entry(
    this.entryId,
    this.transactionId,
    this.accountId,
    this.amount,
    this.recordedAt,
    this.reference
)

fun SelectEntriesForTransaction.toEntryForTransaction() = EntryForTransaction(
    this.entryId,
    this.transactionId,
    this.accountId,
    this.accountName,
    this.currency,
    this.amount,
    this.recordedAt,
    this.reference
)

fun SelectEntriesForAccount.toEntryForAccount() = EntryForAccount(
    this.entryId,
    this.transactionId,
    this.transactionDescription,
    this.transactionIncurredAt,
    this.accountId,
    this.amount,
    this.recordedAt,
    this.reference
)

fun SelectTransactionsInCategoryInRange.toMoneyTransaction() = MoneyTransaction(
    this.transactionId,
    this.categoryId,
    this.number,
    this.incurredAt,
    this.description,
    this.details,
    this.currency,
    this.total
)

fun ExchangeRate.toCurrencyPair() = CurrencyPair(
    this.baseCurrency.toCurrency(),
    this.counterCurrency.toCurrency(),
    this.rate
)