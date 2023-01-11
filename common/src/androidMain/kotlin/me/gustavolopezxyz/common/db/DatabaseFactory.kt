/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import me.gustavolopezxyz.db.Database

actual class DatabaseFactory(private val context: Context) {
    actual fun create(): Database {
        return Database(AndroidSqliteDriver(Database.Schema, context, "wimm.db"))
    }
}

