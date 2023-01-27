/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import me.gustavolopezxyz.common.Config
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.Entry
import java.io.File

actual class DatabaseFactory(val config: Config) {
    actual fun create(): Database {
        val dbFile = this.config.dataFilePath

        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbFile")
        val instantColumnAdapter = InstantColumnAdapter()

        return Database(
            driver,
            accountAdapter = Account.Adapter(EnumColumnAdapter()),
            entryAdapter = Entry.Adapter(instantColumnAdapter, instantColumnAdapter)
        ).also {
            if (File(dbFile).exists()) {
                return it
            }

            Database.Schema.migrate(driver, 0, Database.Schema.version)
        }
    }
}