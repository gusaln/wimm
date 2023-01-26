/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.ext.currentTz
import me.gustavolopezxyz.db.SelectEntriesFromRecord
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EntryRepository : KoinComponent {
    private val db: Database by inject()
    private val entryQueries = db.entryQueries

    fun get(offset: Long = 0, limit: Long = 15): List<Entry> {
        return entryQueries.selectPaginated(offset = offset, limit = limit).executeAsList()
    }

    fun asFlow(offset: Long = 0, limit: Long = 15): Flow<Query<Entry>> {
        return entryQueries.selectPaginated(offset = offset, limit = limit).asFlow()
    }

    fun getByRecordId(recordId: Long): List<SelectEntriesFromRecord> {
        return entryQueries.selectEntriesFromRecord(recordId).executeAsList()
    }

    fun create(entry: Entry) = create(
        entry.description,
        entry.getAmount(),
        entry.account_id,
        entry.record_id,
        entry.incurred_at,
        entry.recorded_at
    )

    fun create(
        description: String,
        amount: Money,
        accountId: Long,
        recordId: Long,
        incurredAt: LocalDateTime,
        recordedAt: LocalDateTime = incurredAt,
    ) = create(
        description,
        amount,
        accountId,
        recordId,
        incurredAt.toInstant(currentTz()),
        recordedAt.toInstant(currentTz()),
    )

    fun create(
        description: String,
        amount: Money,
        accountId: Long,
        recordId: Long,
        incurredAt: Instant,
        recordedAt: Instant = incurredAt,
    ) {
        return entryQueries.insertEntry(
            description,
            accountId,
            recordId,
            amount.currency.code,
            amount.value,
            incurredAt,
            recordedAt,
        )
    }

    fun edit(original: Entry, modified: Entry) {
        if (original.account_id == modified.account_id) {
            editAndNoMove(
                original,
                modified.description,
                modified.getAmount(),
                modified.incurred_at,
                modified.recorded_at,
            )
        } else {
            editAndMove(
                original,
                modified.account_id,
                modified.description,
                modified.getAmount(),
                modified.incurred_at,
                modified.recorded_at,
            )
        }
    }

    private fun editAndNoMove(
        entry: Entry,
        description: String,
        amount: Money,
        incurredAt: Instant,
        recordedAt: Instant
    ) = editAndNoMove(
        entry.id,
        description,
        entry.account_id,
        amount.currency,
        (amount - entry.getAmount()).value,
        incurredAt,
        recordedAt,
    )

    private fun editAndMove(
        entry: Entry,
        accountId: Long,
        description: String,
        amount: Money,
        incurredAt: Instant,
        recordedAt: Instant
    ) {
        editAndMove(
            entry.id,
            description,
            accountId,
            amount.currency,
            (amount - entry.getAmount()).value,
            incurredAt,
            recordedAt,
            entry.account_id,
            entry.amount_value,
        )
    }

    private fun editAndNoMove(
        entryId: Long,
        description: String,
        accountId: Long,
        currency: Currency,
        amountDelta: Double,
        incurredAt: Instant,
        recordedAt: Instant,
    ) {
        return entryQueries.updateEntry(
            id = entryId,
            description = description,
            account_id = accountId,
            amount_currency = currency.toString(),
            amount_delta = amountDelta,
            incurred_at = incurredAt,
            recorded_at = recordedAt
        )
    }

    private fun editAndMove(
        entryId: Long,
        description: String,
        accountId: Long,
        currency: Currency,
        amountDelta: Double,
        incurredAt: Instant,
        recordedAt: Instant,
        originalAccountId: Long,
        originalAmount: Double,
    ) {
        return entryQueries.updateAndMoveEntry(
            id = entryId,
            description = description,
            account_id = accountId,
            amount_currency = currency.toString(),
            amount_delta = amountDelta,
            incurred_at = incurredAt,
            recorded_at = recordedAt,
            original_account_id = originalAccountId,
            original_amount = originalAmount,
        )
    }

    fun delete(entry: Entry) = delete(entry.id, entry.account_id, entry.amount_value)

    fun delete(entryId: Long, accountId: Long, amount: Double) {
        return entryQueries.deleteEntry(entryId, amount, accountId)
    }
}

