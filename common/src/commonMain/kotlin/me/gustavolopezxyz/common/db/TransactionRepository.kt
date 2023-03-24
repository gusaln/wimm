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

    fun get(offset: Int = 0, limit: Int = 15): List<MoneyTransaction> {
        return queries.selectTransactions(offset = offset.toLong(), limit = limit.toLong()).executeAsList()
    }

    fun getAsFlow(offset: Int = 0, limit: Int = 15) =
        queries.selectTransactions(offset = offset.toLong(), limit = limit.toLong()).asFlow()

    fun findById(id: Long): MoneyTransaction? {
        return queries.selectById(id).executeAsOneOrNull()
    }

    fun findByReference(number: Long): MoneyTransaction? {
        return queries.selectByNumber(number).executeAsOneOrNull()
    }

    fun create(categoryId: Long, description: String, details: String?, total: Double): Long {
        val number = generateTransactionNumber()

        queries.insertTransaction(
            number = number,
            categoryId = categoryId,
            description = description.trim(),
            details = if (details.isNullOrBlank()) null else details.trim(),
            total = total
        )

        return number
    }

    fun update(
        transaction: MoneyTransaction,
        categoryId: Long,
        description: String,
        details: String? = null,
        total: Double
    ) =
        update(transaction.transactionId, categoryId, description, details, total)

    fun update(transactionId: Long, categoryId: Long, description: String, details: String?, total: Double) {
        return queries.updateTransaction(
            categoryId = categoryId,
            description = description.trim(),
            details = if (details.isNullOrBlank()) null else details.trim(),
            transactionId = transactionId,
            total = total
        )
    }

    fun delete(transactionId: Long) {
        return queries.deleteTransaction(transactionId)
    }

    private fun generateTransactionNumber() = Clock.System.now().toEpochMilliseconds()
}