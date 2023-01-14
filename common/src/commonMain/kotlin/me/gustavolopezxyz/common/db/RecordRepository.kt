/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import me.gustavolopezxyz.common.ext.getRandomString
import me.gustavolopezxyz.db.Database
import me.gustavolopezxyz.db.Record
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecordRepository : KoinComponent {
    private val db: Database by inject()
    private val queries = db.recordQueries

    fun get(offset: Long = 0, limit: Long = 15): List<Record> {
        return queries.selectPaginated(limit, offset).executeAsList()
    }

    fun create(description: String) {
        return queries.insertRecord(description, getRandomString(36))
    }

    fun update(record: Record, description: String) {
        return queries.updateRecord(description, record.id)
    }
}