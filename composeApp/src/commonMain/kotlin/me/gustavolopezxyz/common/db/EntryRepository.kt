/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.*
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.Entry
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.db.SelectEntriesInRange

class EntryRepository(private val db: Database) {
    private val entryQueries = db.entryQueries

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
        entry.recordedAt
    )

    fun create(
        transactionId: Long,
        accountId: Long,
        amount: Double,
        recordedAt: LocalDateTime,
        reference: String? = null
    ) = create(
        transactionId,
        accountId,
        amount,
        recordedAt.toInstant(currentTimeZone()),
        reference
    )

    fun create(
        transactionId: Long,
        accountId: Long,
        amount: Double,
        recordedAt: Instant,
        reference: String? = null
    ) {
        return entryQueries.insertEntry(
            accountId,
            transactionId,
            amount,
            recordedAt,
            reference
        )
    }

    fun edit(original: Entry, modified: Entry) {
        if (original.accountId == modified.accountId) {
            editAndNoMove(
                original,
                modified.amount,
                modified.recordedAt,
                modified.reference
            )
        } else {
            editAndMove(
                original,
                modified.accountId,
                modified.amount,
                modified.recordedAt,
                modified.reference
            )
        }
    }

    private fun editAndNoMove(
        entry: Entry,
        amount: Double,
        recordedAt: Instant,
        reference: String? = null
    ) = editAndNoMove(
        entry.entryId,
        entry.accountId,
        (amount - entry.amount),
        recordedAt,
        reference
    )

    private fun editAndMove(
        entry: Entry,
        accountId: Long,
        amount: Double,
        recordedAt: Instant,
        reference: String? = null
    ) {
        editAndMove(
            entry.entryId,
            accountId,
            (amount - entry.amount),
            recordedAt,
            entry.accountId,
            entry.amount,
            reference
        )
    }

    private fun editAndNoMove(
        entryId: Long,
        accountId: Long,
        amountDelta: Double,
        recordedAt: Instant,
        reference: String? = null
    ) {
        println("entryId=$entryId accountId=$accountId amountDelta=$amountDelta recordedAt=$recordedAt reference=$reference")

        return entryQueries.updateEntry(
            entryId = entryId,
            accountId = accountId,
            amountDelta = amountDelta,
            recordedAt = recordedAt,
            reference = reference
        )
    }

    private fun editAndMove(
        entryId: Long,
        accountId: Long,
        amountDelta: Double,
        recordedAt: Instant,
        originalAccountId: Long,
        originalAmount: Double,
        reference: String? = null
    ) {
        return entryQueries.updateAndMoveEntry(
            entryId = entryId,
            accountId = accountId,
            amountDelta = amountDelta,
            recordedAt = recordedAt,
            originalAccountId = originalAccountId,
            originalAmount = originalAmount,
            reference = reference
        )
    }

    fun delete(entry: Entry) = delete(entry.entryId, entry.accountId, entry.amount)

    fun delete(entryId: Long, accountId: Long, amount: Double) {
        return entryQueries.deleteEntry(entryId, amount, accountId)
    }
}

