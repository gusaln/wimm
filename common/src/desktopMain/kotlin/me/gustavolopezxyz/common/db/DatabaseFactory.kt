/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import me.gustavolopezxyz.db.Database
import java.io.File

actual class DatabaseFactory {
    actual fun create(): Database {
        var dbExisted: Boolean
        with(File("wimm.db")) {
            dbExisted = exists()
        }

        val driver = JdbcSqliteDriver("jdbc:sqlite:wimm.db")

        if (!dbExisted) {
            Database.Schema.create(driver)
        }

        return Database(driver)
    }
}
