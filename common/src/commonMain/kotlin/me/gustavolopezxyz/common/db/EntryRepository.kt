/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import me.gustavolopezxyz.common.data.Money
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
        return entryQueries.selectPaginated(limit, offset).executeAsList()
    }

    fun asFlow(offset: Long = 0, limit: Long = 15): Flow<Query<Entry>> {
        return entryQueries.selectPaginated(limit, offset).asFlow()
    }

    fun create(
        name: String,
        amount: Money,
        account: Account,
        record: Record,
        incurredAt: Instant,
        recordedAt: Instant = incurredAt,
    ) {
        return create(name, amount, account.id, record.id, incurredAt, recordedAt)
    }

    fun create(
        name: String,
        amount: Money,
        accountId: Long,
        recordId: Long,
        incurredAt: Instant,
        recordedAt: Instant = incurredAt,
    ) {
        return entryQueries.insertEntry(
            name,
            accountId,
            recordId,
            amount.currency.code,
            amount.value,
            incurredAt.toString(),
            recordedAt.toString(),
        )
    }
}

