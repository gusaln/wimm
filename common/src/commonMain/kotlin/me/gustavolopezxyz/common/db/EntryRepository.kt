/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import me.gustavolopezxyz.common.data.Money
import me.gustavolopezxyz.common.ext.currentTz
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
}

