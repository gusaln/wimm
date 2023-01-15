/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import me.gustavolopezxyz.db.Database
import me.gustavolopezxyz.db.Entry
import java.io.File

actual class DatabaseFactory {
    actual fun create(): Database {
        val existed = File("wimm.db").exists()
        val driver = JdbcSqliteDriver("jdbc:sqlite:wimm.db")
        val instantColumnAdapter = InstantColumnAdapter()

        return Database(driver, entryAdapter = Entry.Adapter(instantColumnAdapter, instantColumnAdapter)).also {
//            Database.Schema.migrate(driver, 0, Database.Schema.version)

            if (!existed) {
                Database.Schema.create(driver)
                it.accountQueries.insertAccount("Savings", "USD", 0.0)
            }
        }
    }
}
