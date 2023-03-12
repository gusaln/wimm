/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.*
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.Entry
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.db.SelectEntriesInRange
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EntryRepository : KoinComponent {
    private val db: Database by inject()
    private val entryQueries = db.entryQueries

    fun get(offset: Long = 0, limit: Long = 15) =
        entryQueries.selectEntries(offset = offset, limit = limit).executeAsList()

    fun getAsFlow(offset: Long = 0, limit: Long = 15) =
        entryQueries.selectEntries(offset = offset, limit = limit).asFlow()

    fun getAllForTransactionAsFlow(transactionId: Long) =
        entryQueries.selectEntriesForTransaction(listOf(transactionId)).asFlow()

    fun getAllForTransaction(transactionId: Long) =
        entryQueries.selectEntriesForTransaction(listOf(transactionId)).executeAsList()

    fun getAllForTransactionsAsFlow(transactionIds: Collection<Long>) =
        entryQueries.selectEntriesForTransaction(transactionIds).asFlow()

    fun getAllForTransactions(transactionIds: Collection<Long>) =
        entryQueries.selectEntriesForTransaction(transactionIds).executeAsList()

    fun getAllForAccount(accountId: Long, offset: Long = 0, limit: Long = 15) =
        entryQueries.selectEntriesForAccount(listOf(accountId), offset = offset, limit = limit).asFlow()

    fun getInRangeAsFlow(from: LocalDateTime, to: LocalDateTime): Flow<Query<SelectEntriesInRange>> {
        return entryQueries.selectEntriesInRange(from.toInstant(currentTimeZone()), to.toInstant(currentTimeZone()))
            .asFlow()
    }

//    fun getInRangeAsFlow(range: ClosedRange<LocalDateTime>): Flow<Query<SelectEntriesInRange>> {
//        return getInRangeAsFlow(range.start, range.endInclusive)
//    }

    fun getInRangeAsFlow(from: LocalDate, to: LocalDate): Flow<Query<SelectEntriesInRange>> =
        getInRangeAsFlow(from.atTime(0, 0, 0), to.atTime(23, 59, 59))

    fun getInRangeAsFlow(range: ClosedRange<LocalDate>): Flow<Query<SelectEntriesInRange>> {
        return getInRangeAsFlow(range.start, range.endInclusive)
    }


    fun create(entry: Entry) = create(
        entry.transactionId,
        entry.accountId,
        entry.amount,
        entry.incurredAt,
        entry.recordedAt
    )

    fun create(
        transactionId: Long,
        accountId: Long,
        amount: Double,
        incurredAt: LocalDateTime,
        recordedAt: LocalDateTime = incurredAt,
    ) = create(
        transactionId,
        accountId,
        amount,
        incurredAt.toInstant(currentTimeZone()),
        recordedAt.toInstant(currentTimeZone()),
    )

    fun create(
        transactionId: Long,
        accountId: Long,
        amount: Double,
        incurredAt: Instant,
        recordedAt: Instant = incurredAt,
    ) {
        return entryQueries.insertEntry(
            accountId,
            transactionId,
            amount,
            incurredAt,
            recordedAt,
        )
    }

    fun edit(original: Entry, modified: Entry) {
        if (original.accountId == modified.accountId) {
            editAndNoMove(
                original,
                modified.amount,
                modified.incurredAt,
                modified.recordedAt,
            )
        } else {
            editAndMove(
                original,
                modified.accountId,
                modified.amount,
                modified.incurredAt,
                modified.recordedAt,
            )
        }
    }

    private fun editAndNoMove(
        entry: Entry,
        amount: Double,
        incurredAt: Instant,
        recordedAt: Instant
    ) = editAndNoMove(
        entry.entryId,
        entry.accountId,
        (amount - entry.amount),
        incurredAt,
        recordedAt,
    )

    private fun editAndMove(
        entry: Entry,
        accountId: Long,
        amount: Double,
        incurredAt: Instant,
        recordedAt: Instant
    ) {
        editAndMove(
            entry.entryId,
            accountId,
            (amount - entry.amount),
            incurredAt,
            recordedAt,
            entry.accountId,
            entry.amount,
        )
    }

    private fun editAndNoMove(
        entryId: Long,
        accountId: Long,
        amountDelta: Double,
        incurredAt: Instant,
        recordedAt: Instant,
    ) {
        return entryQueries.updateEntry(
            entryId = entryId,
            accountId = accountId,
            amountDelta = amountDelta,
            incurredAt = incurredAt,
            recordedAt = recordedAt
        )
    }

    private fun editAndMove(
        entryId: Long,
        accountId: Long,
        amountDelta: Double,
        incurredAt: Instant,
        recordedAt: Instant,
        originalAccountId: Long,
        originalAmount: Double,
    ) {
        return entryQueries.updateAndMoveEntry(
            entryId = entryId,
            accountId = accountId,
            amountDelta = amountDelta,
            incurredAt = incurredAt,
            recordedAt = recordedAt,
            originalAccountId = originalAccountId,
            originalAmount = originalAmount,
        )
    }

    fun delete(entry: Entry) = delete(entry.entryId, entry.accountId, entry.amount)

    fun delete(entryId: Long, accountId: Long, amount: Double) {
        return entryQueries.deleteEntry(entryId, amount, accountId)
    }
}

