/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.worldparsers

import kotlinx.datetime.LocalDate
import java.util.*

class ExternalTransaction(
    val description: String,
    val date: LocalDate,
    val reference: String,
    val amount: Double,
) {
    val id: UUID = UUID.randomUUID()
}