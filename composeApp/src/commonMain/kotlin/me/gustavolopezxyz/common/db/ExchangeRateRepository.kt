/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import app.cash.sqldelight.Query
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toInstant
import me.gustavolopezxyz.common.data.ExchangeRate
import me.gustavolopezxyz.common.ext.datetime.atStartOfDay
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.common.ext.datetime.now
import me.gustavolopezxyz.common.money.Currency

class ExchangeRateRepository(private val db: Database) {
    private val queries = db.exchangeRateQueries

    fun get(offset: Int = 0, limit: Int = 15): List<ExchangeRate> {
        return queries.selectAllPaginated(offset = offset.toLong(), limit = limit.toLong()).executeAsList()
    }

    fun selectAllPaginated(offset: Long, limit: Long): Query<ExchangeRate> =
        queries.selectAllPaginated(offset = offset, limit = limit)

    fun findCurrentByCurrencies(base: Currency, counter: Currency): ExchangeRate =
        findNextByCurrencies(base, counter, now())

    fun findCurrentByCurrenciesOrNull(base: Currency, counter: Currency): ExchangeRate? =
        findNextByCurrenciesOrNull(base, counter, now())

    fun findNextByCurrencies(base: Currency, counter: Currency, date: LocalDate): ExchangeRate =
        findNextByCurrencies(base, counter, date.atStartOfDay().toInstant(currentTimeZone()))

    fun findNextByCurrenciesOrNull(base: Currency, counter: Currency, date: LocalDate): ExchangeRate? =
        findNextByCurrenciesOrNull(base, counter, date.atStartOfDay().toInstant(currentTimeZone()))

    private fun findNextByCurrencies(base: Currency, counter: Currency, date: Instant): ExchangeRate {
        return queries.selectByCurrenciesSince(base.code, counter.code, date).executeAsOne()
    }

    private fun findNextByCurrenciesOrNull(base: Currency, counter: Currency, date: Instant): ExchangeRate? {
        return queries.selectByCurrenciesSince(base.code, counter.code, date).executeAsOneOrNull()
    }

    fun create(
        base: Currency,
        counter: Currency,
        rate: Double,
        effectiveSince: LocalDate
    ) {
        queries.insert(
            baseCurrency = base.code,
            counterCurrency = counter.code,
            rate = rate,
            effectiveSince = effectiveSince.atStartOfDay().toInstant(currentTimeZone())
        )
    }
}