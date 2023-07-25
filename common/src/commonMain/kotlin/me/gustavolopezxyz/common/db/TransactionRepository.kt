/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
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

    fun create(
        categoryId: Long,
        incurredAt: LocalDateTime,
        description: String,
        details: String?,
        currency: Currency,
        total: Double
    ): Long {
        val number = generateTransactionNumber()

        queries.insertTransaction(
            number = number,
            categoryId = categoryId,
            incurredAt = incurredAt.toInstant(currentTimeZone()),
            description = description.trim(),
            details = if (details.isNullOrBlank()) null else details.trim(),
            currency = currency.code,
            total = total
        )

        return number
    }

    fun update(
        transaction: MoneyTransaction,
        categoryId: Long,
        incurredAt: LocalDateTime,
        description: String,
        details: String? = null,
        currency: Currency,
        total: Double
    ) =
        update(transaction.transactionId, categoryId, incurredAt, description, details, currency, total)

    fun update(
        transactionId: Long,
        categoryId: Long,
        incurredAt: LocalDateTime,
        description: String,
        details: String?,
        currency: Currency,
        total: Double
    ) {
        return queries.updateTransaction(
            categoryId = categoryId,
            incurredAt = incurredAt.toInstant(currentTimeZone()),
            description = description.trim(),
            details = if (details.isNullOrBlank()) null else details.trim(),
            transactionId = transactionId,
            currency = currency.code,
            total = total
        )
    }

    fun delete(transactionId: Long) {
        return queries.deleteTransaction(transactionId)
    }

    private fun generateTransactionNumber() = Clock.System.now().toEpochMilliseconds()
}