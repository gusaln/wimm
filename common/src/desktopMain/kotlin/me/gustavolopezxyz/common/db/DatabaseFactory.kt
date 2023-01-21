/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.db.AfterVersion
import com.squareup.sqldelight.db.migrateWithCallbacks
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import me.gustavolopezxyz.db.Database
import me.gustavolopezxyz.db.Entry
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute

actual class DatabaseFactory {
    actual fun create(): Database {
        val dbFile = getDatabasePath()

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

    private fun getDatabasePath(): String {
        val paths = listOfNotNull(
            System.getenv("XDG_DATA_HOME").let {
                if (it != null) {
                    Path(it, "wimm", "data.db").absolute().toString()
                } else {
                    null
                }

            },
            System.getenv("HOME").let {
                if (it != null) {
                    Path(it, ".wimm.db").absolute().toString()
                } else {
                    null
                }
            },
            "wimm.db"
        )


        paths.forEach {
            if (File(it).exists()) {
                return it
            }
        }

        paths.forEach {
            if (isValidDatabasePath(it)) {
                return it
            }
        }

        return Path("wimm.db").toString()
    }

    private fun isValidDatabasePath(path: String): Boolean = with(File(path).parentFile) {
        return canRead() && canWrite()
    }
}