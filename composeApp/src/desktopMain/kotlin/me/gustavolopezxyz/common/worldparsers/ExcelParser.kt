/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.worldparsers

import org.dhatim.fastexcel.reader.ReadableWorkbook
import org.dhatim.fastexcel.reader.Row
import org.dhatim.fastexcel.reader.Sheet
import java.io.FileInputStream

internal const val ROW_FIRST_CONTENT = 10
internal const val COL_TYPE_OF_TRANSACTION = 10

class ExcelParser(
    private val fileLocation: String,
    private val firstContentRow: Int = 1,
) {
    fun parse(onRow: (Row?) -> Unit) {
        FileInputStream(fileLocation).use { file ->
            ReadableWorkbook(file).use { wb ->
                val sheet: Sheet = wb.firstSheet

                sheet.openStream().use { rows ->
                    rows.filter { row -> row.rowNum >= firstContentRow }.forEach { r -> onRow(r) }
                }
            }
        }
    }

    fun parseIgnoringNulls(onRow: (Row) -> Unit) {
        FileInputStream(fileLocation).use { file ->
            ReadableWorkbook(file).use { wb ->
                val sheet: Sheet = wb.firstSheet

                sheet.openStream().use { rows ->
                    rows.filter { row -> row != null && row.rowNum >= firstContentRow }.forEach { r -> onRow(r) }
                }
            }
        }
    }
}

