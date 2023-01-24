/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.db.AfterVersion
import com.squareup.sqldelight.db.migrateWithCallbacks
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import me.gustavolopezxyz.common.Config
import me.gustavolopezxyz.db.Database
import me.gustavolopezxyz.db.Entry
import java.io.File

actual class DatabaseFactory(val config: Config) {
    actual fun create(): Database {
        val dbFile = this.config.dataFilePath

        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbFile")
        val instantColumnAdapter = InstantColumnAdapter()

        return Database(driver, entryAdapter = Entry.Adapter(instantColumnAdapter, instantColumnAdapter)).also {
            if (File(dbFile).exists()) {
                return it
            }

            Database.Schema.migrateWithCallbacks(
                driver,
                0,
                Database.Schema.version,
                AfterVersion(1) {
                    it.accountQueries.insertAccount("Savings", "USD", 0.0)
                }
            )
        }
    }
}