/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import kotlinx.datetime.Instant

data class CategoryWithParent(
    val categoryId: Long,
    val parentCategoryId: Long?,
    val parentCategoryName: String?,
    val name: String,
    val isLocked: Boolean,
) {
    fun toCategory() = Category(
        this.categoryId, this.parentCategoryId, this.name, this.isLocked
    )

    fun fullname() = if (parentCategoryName != null) "$parentCategoryName / $name" else name
}

data class EntryForTransaction(
    val entryId: Long,
    val transactionId: Long,
    val accountId: Long,
    val accountName: String,
    val currency: String,
    val amount: Double,
    val recordedAt: Instant,
    val reference: String?,
)

data class EntryForAccount(
    val entryId: Long,
    val transactionId: Long,
    val transactionDescription: String,
    val transactionIncurredAt: Instant,
    val accountId: Long,
    val amount: Double,
    val recordedAt: Instant,
    val reference: String?,
)