/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

class InstantColumnAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long) = Instant.fromEpochSeconds(databaseValue)
    override fun encode(value: Instant) = value.epochSeconds
}