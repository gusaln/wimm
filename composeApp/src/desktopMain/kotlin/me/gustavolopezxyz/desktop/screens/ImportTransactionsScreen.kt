/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.ext.datetime.nowLocalDateTime
import me.gustavolopezxyz.common.ext.datetime.toSimpleFormat
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.worldparsers.ExcelParser
import me.gustavolopezxyz.common.worldparsers.ExternalTransaction
import me.gustavolopezxyz.desktop.navigation.ImportTransactionsComponent
import me.gustavolopezxyz.desktop.ui.common.AppButton
import me.gustavolopezxyz.desktop.ui.common.AppTextButton
import me.gustavolopezxyz.desktop.ui.common.ScreenTitle
import java.io.File


@Composable
fun ImportTransactionsScreen(component: ImportTransactionsComponent) {
    val filePath by component.filePath.subscribeAsState()
    val fileExists by component.fileExists.subscribeAsState()
    val bankTransactions by component.bankTransactions.subscribeAsState()
//
//
//    fun readFile(filePath: String) {
//        val reader = ExcelParser(
//            fileLocation = filePath,
//            firstContentRow = 10
//        )
//
//        reader.parseIgnoringNulls { row ->
//            val date = row.getCellRawValue(1).let {
//                if (it.isPresent) {
//                    val parts = it.get().split('/')
//
//                    LocalDate(
//                        year = parts[2].take(4).toInt(),
//                        monthNumber = parts[1].take(2).toInt(),
//                        dayOfMonth = parts[0].take(2).toInt()
//                    )
//                } else {
//                    nowLocalDateTime().date
//
//                }
//
//            }
//
//            val reference = row.getCellRawValue(2).let {
//                if (it.isPresent) it.get().trimStart('0') else ""
//            }
//
//            val description = row.getCellRawValue(3).let {
//                if (it.isPresent) it.get().trim() else ""
//            }
//
//            val amount = row.getCellRawValue(4).let {
//                if (it.isPresent) it.get().toDouble() else 0.0
//            }
//
//            bankTransactions.add(ExternalTransaction(description, date, reference, amount))
//        }
//    }
//
//    LaunchedEffect(filePath) {
//        fileExists = filePath.isNotBlank() && File(filePath).exists()
//    }

    val scroll = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scroll).padding(AppDimensions.Default.padding.large),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
    ) {
        ScreenTitle { Text("Import transactions") }

        OutlinedTextField(
            value = filePath,
            onValueChange = { component.setFilePath(it.trimStart()) },
            label = {
                Text("Select the file path")
            },
            trailingIcon = {
                if (fileExists) {
                    IconButton(onClick = { component.readFile() }, enabled = bankTransactions.isEmpty()) {
                        Icon(Icons.Default.Upload, "process file")
                    }
                }
            }
        )

        Spacer(Modifier.fillMaxWidth().height(6.dp))

        if (bankTransactions.isNotEmpty()) {
            Column {
                bankTransactions.forEach {
                    Row(Modifier.padding(0.dp, 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(it.id.toString(), modifier = Modifier.weight(1.0f))
                        Text(it.description, modifier = Modifier.weight(1.0f))
                        Text(it.reference, modifier = Modifier.weight(1.0f))
                        Text(it.date.toSimpleFormat(), modifier = Modifier.width(128.dp))
                        Text(it.amount.toString(), modifier = Modifier.width(128.dp))
                    }
                }
            }
        }

        Spacer(Modifier.fillMaxWidth().height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small, Alignment.End)
        ) {
            AppButton(onClick = {component.onImportTransaction()}, "Create")

            if (component.onCancel != null) {
                AppTextButton(onClick = { component.onCancel.invoke() }, "Cancel")
            }
        }
    }
}