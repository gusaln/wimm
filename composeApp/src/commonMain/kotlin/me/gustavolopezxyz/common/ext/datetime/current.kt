/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ext.datetime

import kotlinx.datetime.*

fun currentTimeZone(): TimeZone = TimeZone.currentSystemDefault()

fun now(): Instant = Clock.System.now()

fun nowLocalDateTime(): LocalDateTime = now().toLocalDateTime(currentTimeZone())

