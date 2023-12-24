/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import androidx.compose.runtime.Immutable
import kotlinx.datetime.*
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.db.SelectEntriesForTransaction
import java.util.*

internal fun newEntryId(): Long {
//    val id: Long = -Clock.System.now().toEpochMilliseconds(),
    return -UUID.randomUUID().leastSignificantBits
}

@Immutable
data class NewEntryDto(
    val id: Long = newEntryId(),
    val accountId: Long? = null,
    val currency: Currency,
    val amount: Double = 0.0,
    val recordedAt: LocalDate,
    val reference: String? = null,
) {
    constructor(
        id: Long = newEntryId(),
        account: Account? = null,
        amount: Double = 0.0,
        recordedAt: LocalDate,
        reference: String? = null,
    ) : this(
        id,
        account?.accountId,
        account?.currency?.toCurrency() ?: MissingCurrency,
        amount,
        recordedAt,
        reference
    )
}

fun emptyNewEntryDto(recordedAt: LocalDate) = NewEntryDto(recordedAt = recordedAt)

inline fun emptyNewEntryDto() = emptyNewEntryDto(Clock.System.now().toLocalDateTime(currentTimeZone()).date)


@Immutable
data class ModifiedEntryDto(
    val id: Long,
    val accountId: Long,
    val accountName: String,
    val currency: String,
    val amount: Double,
    val recordedAt: LocalDate,
    val reference: String? = null,
    val wasEdited: Boolean = false,
    val toDelete: Boolean = false,
) {
    fun edit(
        amount: Double = this.amount,
        recordedAt: LocalDate = this.recordedAt,
        reference: String? = this.reference,
    ) = copy(
        amount = amount,
        recordedAt = recordedAt,
        reference = reference,
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
        recordedAt = recordedAt.atTime(0, 0, 0).toInstant(
            currentTimeZone()
        ),
        reference = reference
    )
}

fun modifiedEntryDto(entry: SelectEntriesForTransaction): ModifiedEntryDto {
    return ModifiedEntryDto(
        id = entry.entryId,
        accountId = entry.accountId,
        accountName = entry.accountName,
        currency = entry.currency,
        amount = entry.amount,
        recordedAt = entry.recordedAt.toLocalDateTime(currentTimeZone()).date,
        reference = entry.reference
    )
}