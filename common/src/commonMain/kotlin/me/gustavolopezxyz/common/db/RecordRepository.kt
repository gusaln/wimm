/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.Record
import me.gustavolopezxyz.common.ext.getRandomString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecordRepository : KoinComponent {
    private val db: Database by inject()
    private val queries = db.recordQueries

    fun get(offset: Long = 0, limit: Long = 15): List<Record> {
        return queries.selectPaginated(limit, offset).executeAsList()
    }

    fun findById(id: Long): Record? {
        return queries.selectById(id).executeAsOneOrNull()
    }

    fun findByReference(reference: String): Record? {
        return queries.selectByReference(reference).executeAsOneOrNull()
    }

    fun create(description: String): String {
        val reference = getRandomString(36)

        queries.insertRecord(description, reference)

        return reference
    }

    fun update(record: Record, description: String) = queries.updateRecord(description, record.id)

    fun update(recordId: Long, description: String) {
        return queries.updateRecord(description, recordId)
    }

    fun delete(recordId: Long) {
        return queries.deleteRecord(recordId)
    }
}