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
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.Money
import me.gustavolopezxyz.common.ext.currentTz
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.db.Account
import me.gustavolopezxyz.db.Database
import me.gustavolopezxyz.db.Entry
import me.gustavolopezxyz.db.Record
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

    fun getByRecordId(recordId: Long): List<Entry> {
        return entryQueries.selectFromRecord(recordId).executeAsList()
    }

    fun create(
        description: String,
        amount: Money,
        account: Account,
        record: Record,
        incurredAt: LocalDateTime,
        recordedAt: LocalDateTime = incurredAt,
    ) {
        return create(description, amount, account.id, record.id, incurredAt, recordedAt)
    }

    fun create(
        description: String,
        amount: Money,
        accountId: Long,
        recordId: Long,
        incurredAt: LocalDateTime,
        recordedAt: LocalDateTime = incurredAt,
    ) {
        return entryQueries.insertEntry(
            description,
            accountId,
            recordId,
            amount.currency.code,
            amount.value,
            incurredAt.toInstant(currentTz()),
            recordedAt.toInstant(currentTz()),
        )
    }

    fun edit(oldEntry: Entry, modifiedEntry: Entry) {
        return edit(
            oldEntry.id,
            modifiedEntry.description,
            modifiedEntry.account_id,
            modifiedEntry.amount_currency.toCurrency(),
            modifiedEntry.amount_value - oldEntry.amount_value,
            modifiedEntry.incurred_at,
            modifiedEntry.recorded_at,
        )
    }

    fun edit(
        entryId: Long,
        description: String,
        accountId: Long,
        currency: Currency,
        amountDelta: Double,
        incurredAt: LocalDateTime,
        recordedAt: LocalDateTime,
    ) {
        return edit(
            entryId,
            description,
            accountId,
            currency,
            amountDelta,
            incurredAt.toInstant(currentTz()),
            recordedAt.toInstant(currentTz()),
        )
    }

    fun edit(
        entryId: Long,
        description: String,
        accountId: Long,
        currency: Currency,
        amountDelta: Double,
        incurredAt: Instant,
        recordedAt: Instant,
    ) {
        return entryQueries.updateEntry(
            description, accountId, currency.toString(), amountDelta, incurredAt, recordedAt, entryId
        )
    }

    fun delete(entry: Entry) = delete(entry.id, entry.account_id, entry.amount_value)

    fun delete(entryId: Long, accountId: Long, amount: Double) {
        return entryQueries.deleteEntry(entryId, amount, accountId)
    }
}

