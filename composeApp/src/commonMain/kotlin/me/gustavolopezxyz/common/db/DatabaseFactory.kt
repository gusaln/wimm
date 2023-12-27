/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db


expect class DatabaseFactory {
    fun create(): Database
}
