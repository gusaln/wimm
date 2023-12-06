/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ext.datetime

import kotlinx.datetime.*

fun LocalDate.atStartOfDay() = this.atTime(0, 0, 0)

fun LocalDate.atEndOfDay() = this.atTime(23, 59, 59)

fun LocalDate.startOfMonth() = this + DatePeriod(days = -(this.dayOfMonth - 1))

fun LocalDate.endOfMonth() = this + DatePeriod(months = 1, days = -this.dayOfMonth)

fun LocalDate.rangeToEndOfMonth() = this..this.endOfMonth()

fun LocalDate.nextMonth() = this + DatePeriod(months = 1)

fun LocalDate.prevMonth() = this + DatePeriod(months = -1)


fun LocalDateTime.startOfMonth() = this.date.startOfMonth().atStartOfDay()

fun LocalDateTime.endOfMonth() = this.date.endOfMonth().atEndOfDay()

fun LocalDateTime.rangeToEndOfMonth() = this..this.endOfMonth()

fun LocalDateTime.nextMonth() = this.date.nextMonth().atTime(this.time)

fun LocalDateTime.prevMonth() = this.date.prevMonth().atTime(this.time)
