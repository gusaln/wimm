/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.Entry

actual class DatabaseFactory(private val context: Context) {
    actual fun create(): Database {
        val instantColumnAdapter = InstantColumnAdapter()

        return Database(
            AndroidSqliteDriver(Database.Schema, context, "wimm.db"),
            entryAdapter = Entry.Adapter(instantColumnAdapter, instantColumnAdapter)
        )
    }
}

