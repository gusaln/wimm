/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import kotlinx.datetime.Instant
import me.gustavolopezxyz.common.ext.toMoney

data class CategoryWithParent(
    val categoryId: Long,
    val parentCategoryId: Long?,
    val parentCategoryName: String?,
    val name: String,
    val isLocked: Boolean,
) {
    fun toCategory() = Category(
        this.categoryId,
        this.parentCategoryId,
        this.name,
        this.isLocked
    )

    fun fullname() = if (parentCategoryName != null) "$parentCategoryName / $name" else name
}

data class DenormalizedEntry(
    val entryId: Long,
    val transactionId: Long,
    val transactionDescription: String,
    val accountId: Long,
    val accountName: String,
    val currency: String,
    val amount: Double,
    val incurredAt: Instant,
    val recordedAt: Instant,
) {
    val amountAsMoney get() = amount.toMoney(currency)
}