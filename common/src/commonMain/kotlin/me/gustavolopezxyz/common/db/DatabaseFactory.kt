/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import me.gustavolopezxyz.db.Database

expect class DatabaseFactory {
    fun create(): Database
}
