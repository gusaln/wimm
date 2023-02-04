/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import kotlinx.datetime.*
import me.gustavolopezxyz.common.ext.currentTz
import me.gustavolopezxyz.db.SelectEntriesForTransaction

data class NewEntryDto(
    val id: Long = -Clock.System.now().toEpochMilliseconds(),
    val account: Account? = null,
    val amount: Double = 0.0,
    val incurredAt: LocalDate,
    val recordedAt: LocalDate = incurredAt
)

fun emptyNewEntryDto() = NewEntryDto(incurredAt = Clock.System.now().toLocalDateTime(currentTz()).date)


data class ModifiedEntryDto(
    val id: Long,
    val accountId: Long,
    val accountName: String,
    val currency: String,
    val amount: Double,
    val incurredAt: LocalDate,
    val recordedAt: LocalDate,
    val wasEdited: Boolean = false,
    val toDelete: Boolean = false,
) {
    fun edit(
        amount: Double = this.amount,
        incurredAt: LocalDate = this.incurredAt,
        recordedAt: LocalDate = this.recordedAt,
    ) = copy(
        amount = amount,
        incurredAt = incurredAt,
        recordedAt = recordedAt,
        wasEdited = true,
        toDelete = false,
    )

    fun changeAccount(account: Account) = copy(
        accountId = account.accountId,
        accountName = account.name,
        currency = account.currency,
        wasEdited = true,
        toDelete = false,
    )

    fun delete() = copy(toDelete = true)

    fun restore() = copy(toDelete = false)

    fun toEntry(transactionId: Long): Entry = Entry(
        entryId = id,
        transactionId = transactionId,
        accountId = accountId,
        amount = amount,
        incurredAt = incurredAt.atTime(0, 0, 0).toInstant(
            currentTz()
        ),
        recordedAt = recordedAt.atTime(0, 0, 0).toInstant(
            currentTz()
        )
    )
}

fun modifiedEntryDto(entry: SelectEntriesForTransaction): ModifiedEntryDto {
    return ModifiedEntryDto(
        id = entry.entryId,
        accountId = entry.accountId,
        accountName = entry.accountName,
        currency = entry.currency,
        amount = entry.amount,
        incurredAt = entry.incurredAt.toLocalDateTime(currentTz()).date,
        recordedAt = entry.recordedAt.toLocalDateTime(currentTz()).date,
    )
}