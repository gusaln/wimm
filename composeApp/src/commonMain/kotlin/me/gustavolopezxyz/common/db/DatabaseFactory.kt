/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import me.gustavolopezxyz.common.data.Database


expect class DatabaseFactory {
    fun create(): Database
}
