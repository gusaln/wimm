/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import me.gustavolopezxyz.db.SelectAllCategories
import me.gustavolopezxyz.db.SelectEntries
import me.gustavolopezxyz.db.SelectEntriesFromTransaction


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

fun SelectAllCategories.toCategory() = Category(
    this.categoryId,
    this.parentCategoryId,
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

fun SelectEntriesFromTransaction.toEntry() = Entry(
    this.entryId,
    this.transactionId,
    this.accountId,
    this.amount,
    this.incurredAt,
    this.recordedAt
)

fun SelectEntries.toDto() = DenormalizedEntry(
    this.entryId,
    this.transactionId,
    this.transactionDescription,
    this.accountId,
    this.accountName,
    this.currency,
    this.amount,
    this.incurredAt,
    this.recordedAt,
)