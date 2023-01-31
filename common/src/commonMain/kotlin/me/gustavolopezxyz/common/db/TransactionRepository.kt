/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.datetime.Clock
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.MoneyTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionRepository : KoinComponent {
    private val db: Database by inject()
    private val queries = db.transactionQueries

    fun get(offset: Long = 0, limit: Long = 15): List<MoneyTransaction> {
        return queries.selectTransactions(limit, offset).executeAsList()
    }

    fun getAsFlow(offset: Long = 0, limit: Long = 15) =
        queries.selectTransactions(offset = offset, limit = limit).asFlow()

    fun findById(id: Long): MoneyTransaction? {
        return queries.selectById(id).executeAsOneOrNull()
    }

    fun findByReference(number: Long): MoneyTransaction? {
        return queries.selectByNumber(number).executeAsOneOrNull()
    }

    fun create(categoryId: Long, description: String): Long {
        val number = generateTransactionNumber()

        queries.insertTransaction(number = number, categoryId = categoryId, description = description)

        return number
    }

    fun update(transaction: MoneyTransaction, categoryId: Long, description: String) =
        update(transaction.transactionId, categoryId, description)

    fun update(transactionId: Long, categoryId: Long, description: String) {
        return queries.updateTransaction(
            categoryId = categoryId,
            description = description,
            transactionId = transactionId
        )
    }

    fun delete(transactionId: Long) {
        return queries.deleteTransaction(transactionId)
    }

    private fun generateTransactionNumber() = Clock.System.now().toEpochMilliseconds()
}