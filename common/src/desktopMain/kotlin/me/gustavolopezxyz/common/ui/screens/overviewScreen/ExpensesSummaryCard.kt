/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens.overviewScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.data.MissingCategory
import me.gustavolopezxyz.common.data.Palette
import me.gustavolopezxyz.common.data.currencyOf
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.ext.datetime.*
import me.gustavolopezxyz.common.ui.common.AppListTitle
import me.gustavolopezxyz.common.ui.core.MoneyPartitionEntry
import me.gustavolopezxyz.common.ui.core.MoneyPartitionSummary
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.db.SelectEntriesInRange

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
    entryRepository: EntryRepository,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit
) {
    var month by remember { mutableStateOf(nowLocalDateTime().date) }

    val categories by categoryRepository.allAsFlow().mapToList().map { list ->
        list.map { it.toDto() }.associateBy { it.categoryId }
    }.collectAsState(emptyMap())

    var expenses by remember { mutableStateOf(emptyList<SelectEntriesInRange>()) }
    LaunchedEffect(month) {
        entryRepository.getInRangeAsFlow(month.startOfMonth().rangeToEndOfMonth()).mapToList()
            .map { list ->
                list.filter { it.transactionTotal < 0 }
            }.collect {
                expenses = it
            }
    }

    MoneyPartitionSummary(
        currency = currencyOf("USD"),
        amounts = expenses.groupBy {
            categories.getOrDefault(
                it.transactionCategoryId,
                MissingCategory.toDto()
            ).fullname()
        }.entries.map { entry -> MoneyPartitionEntry(entry.key, entry.value.sumOf { it.amount }) },
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