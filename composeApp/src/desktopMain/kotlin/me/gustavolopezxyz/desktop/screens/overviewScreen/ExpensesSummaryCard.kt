/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens.overviewScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.datetime.*
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.ui.common.AppListTitle
import me.gustavolopezxyz.desktop.ui.core.MoneyPartitionEntry
import me.gustavolopezxyz.desktop.ui.core.MoneyPartitionSummary

internal val expensesColors = listOfNotNull(
    Palette.Colors["red800"],
    Palette.Colors["red600"],
    Palette.Colors["red400"],
    Palette.Colors["red200"],
    Palette.Colors["orange800"],
    Palette.Colors["orange600"],
    Palette.Colors["orange400"],
    Palette.Colors["orange200"],
    Palette.Colors["yellow800"],
    Palette.Colors["yellow600"],
    Palette.Colors["yellow400"],
    Palette.Colors["yellow200"],
)

@Composable
fun ExpensesSummaryCard(
    categoryRepository: CategoryRepository,
    transactionRepository: TransactionRepository,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit
) {
    var month by remember { mutableStateOf(nowLocalDateTime().date) }

    val categories by categoryRepository.allAsFlow().mapToList(Dispatchers.IO).map { list ->
        list.map { it.toDto() }.associateBy { it.categoryId }
    }.collectAsState(emptyMap())

    var expenses by remember { mutableStateOf(emptyList<MoneyTransaction>()) }
    LaunchedEffect(month) {
        transactionRepository.getInPeriodAsFlow(month.startOfMonth().atStartOfDay(), month.endOfMonth().atEndOfDay())
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.filter { it.total < 0 }
            }.collect {
                expenses = it
            }
    }

    MoneyPartitionSummary(
        currency = currencyOf("USD"),
        amounts = expenses.groupBy {
            categories.getOrDefault(
                it.categoryId,
                MissingCategory.toDto()
            ).fullname()
        }.entries.map { transaction -> MoneyPartitionEntry(transaction.key, transaction.value.sumOf { it.total }) },
        descending = false,
        colorPalette = expensesColors,
        modifier = modifier
    ) {
        AppListTitle(verticalAlignment = Alignment.Top) {
            actions()

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(month.month.toString())

                IconButton(onClick = { month = month.prevMonth() }) {
                    Icon(Icons.Default.KeyboardArrowLeft, "prev month")
                }

                IconButton(onClick = { month = month.nextMonth() }) {
                    Icon(Icons.Default.KeyboardArrowRight, "next month")
                }
            }
        }
    }
}