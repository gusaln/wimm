/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ext.datetime

import kotlinx.datetime.*

internal fun currentYear(): Int = nowLocalDateTime().year

fun LocalTime.toSimpleFormat(): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

fun LocalDate.toSimpleFormat(): String {
    val dt = this

    return buildString {
        append(dt.dayOfMonth)
        append(' ')
        append(dt.month.name.take(3))
        append('.')

        if (dt.year != currentYear()) {
            append(' ')
            append(dt.year)
        }
    }
}

fun LocalDateTime.toSimpleFormat(): String = "${date.toSimpleFormat()} at ${time.toSimpleFormat()}"

fun Instant.formatDateTime(): String = toLocalDateTime(currentTimeZone()).toSimpleFormat()

fun Instant.formatDate(): String = toLocalDateTime(currentTimeZone()).date.toSimpleFormat()
