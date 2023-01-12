/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import me.gustavolopezxyz.db.Database
import java.io.File

actual class DatabaseFactory {
    actual fun create(): Database {
        val driver = JdbcSqliteDriver("jdbc:sqlite:wimm.db")

        with(File("wimm.db")) {
            if (!exists()) {
                Database.Schema.create(driver)

                return Database(driver).also {
                    it.accountQueries.insertAccount("Savings", "USD", 0.0)
                }
            }
        }


        return Database(driver)
    }
}
