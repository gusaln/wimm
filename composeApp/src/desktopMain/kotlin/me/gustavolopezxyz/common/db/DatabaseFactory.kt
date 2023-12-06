/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.Entry
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
            moneyTransactionAdapter = MoneyTransaction.Adapter(instantColumnAdapter)
        ).also {
            if (File(dbFile).exists()) {
                Database.Schema.migrate(driver, getLastMigration(), Database.Schema.version)
            } else {
                Database.Schema.migrate(driver, 0, Database.Schema.version)
            }

            updateLastMigration()
        }
    }

    private fun getLastMigration(): Long {
        val lastMigrationFile = getLastMigrationFile()

        if (lastMigrationFile.isFile && lastMigrationFile.canRead()) return lastMigrationFile.readText().trim().toLong()

        return Database.Schema.version
    }

    private fun updateLastMigration() {
        val lastMigrationFile = getLastMigrationFile()

        lastMigrationFile.writeText(Database.Schema.version.toString())
    }

    private fun getLastMigrationFile(): File = File("${config.dataFilePath}.schema-version")
}