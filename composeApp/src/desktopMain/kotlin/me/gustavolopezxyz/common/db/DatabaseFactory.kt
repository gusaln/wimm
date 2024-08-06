/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.JdbcPreparedStatement
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.datetime.Instant
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.Entry
import me.gustavolopezxyz.common.data.ExchangeRate
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.desktop.Config
import java.io.File

actual class DatabaseFactory(private val config: Config) {
    actual fun create(): Database {
        val dbFile = this.config.dataFilePath

        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbFile")
        val instantColumnAdapter = InstantColumnAdapter()

        return Database(
            driver,
            accountAdapter = Account.Adapter(EnumColumnAdapter()),
            entryAdapter = Entry.Adapter(instantColumnAdapter),
            exchangeRateAdapter = ExchangeRate.Adapter(instantColumnAdapter),
            moneyTransactionAdapter = MoneyTransaction.Adapter(instantColumnAdapter),
        ).also {
            if (File(dbFile).exists()) {
                var migration = 6L
                try {
                    migration = it.migrationQueries.select().executeAsOne().migrationId ?: 6
                } catch (_: Error) {
                }

                Database.Schema.migrate(driver, migration, Database.Schema.version)
            } else {
                Database.Schema.migrate(driver, 0, Database.Schema.version)
            }
        }
    }
}