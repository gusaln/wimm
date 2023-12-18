/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import app.cash.sqldelight.coroutines.mapToList
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import kotlinx.coroutines.Dispatchers
import me.gustavolopezxyz.common.data.LaunchOnBottomReachedEffect
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.data.rememberLazyPaginationState
import me.gustavolopezxyz.common.data.toEntryForTransaction
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.dropdownSelected
import me.gustavolopezxyz.common.ui.theme.dropdownUnselected
import me.gustavolopezxyz.desktop.navigation.OverviewComponent
import me.gustavolopezxyz.desktop.navigation.SummaryType
import me.gustavolopezxyz.desktop.screens.overviewScreen.*
import me.gustavolopezxyz.desktop.ui.common.ContainerLayout

@Composable
fun OverviewScreen(component: OverviewComponent) {
    val summary by component.summary.subscribeAsState()

    val categoriesById by component.getCategoriesByIdAsFlow().collectAsState(emptyMap())

    val pagination = rememberLazyPaginationState<MoneyTransaction>(TRANSACTIONS_PAGE_SIZE) {
        component.getTransactionsAsFlow(1, itemsLoadedCount())
            .mapToList(Dispatchers.IO)
            .collect {
                items = it
            }
    }
//    LaunchedEffect(pagination.pagesLoaded) {
//        pagination.isLoading = true
//        component.getTransactionsAsFlow(1, pagination.itemsLoadedCount())
//            .mapToList(Dispatchers.IO)
//            .collect {
//                pagination.items = it
//                pagination.isLoading = false
//            }
//    }

    val entriesByTransaction by derivedStateOf {
        component
            .getEntries(pagination.items.map { it.transactionId })
            .map { it.toEntryForTransaction() }
            .groupBy { it.transactionId }
    }

    val listState = rememberLazyListState()
    listState.LaunchOnBottomReachedEffect(buffer = 4) {
        if (pagination.items.isNotEmpty() && !pagination.isLoading && pagination.itemsLoadedCount() < it.minimumRequiredItemsLoadedCount()) {
            pagination.loadUpToPage(pagination.pagesLoaded + 1)
//            KoinJavaComponent.getKoin().logger.info("[TransactionsOverviewCard] ${pagination.pagesLoaded} pages loaded.")
        }
    }

    ContainerLayout {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    AppDimensions.Default.spacing.large, Alignment.CenterHorizontally
                )
            ) {
                TransactionsOverviewCard(
                    listState = listState,
                    categoriesById = categoriesById,
                    transactions = pagination.items,
                    entriesByTransaction = entriesByTransaction,
                    isLoading = pagination.isLoading,
                    modifier = Modifier.fillMaxHeight().weight(1f)
                ) {
                    component.onEditTransaction(it.transactionId)
                }

                when (summary) {
                    SummaryType.Balance -> {
                        BalancePartitionSummaryCard(component.accountRepository, Modifier.weight(2f)) {
                            SummaryTypeDropdown(summary, onClick = { component.onShowSummary(it) })
                        }
                    }

                    SummaryType.Owned -> {
                        OwnedPartitionSummaryCard(component.accountRepository, Modifier.weight(2f)) {
                            SummaryTypeDropdown(summary, onClick = { component.onShowSummary(it) })
                        }
                    }

                    SummaryType.Debt -> {
                        DebtPartitionSummaryCard(component.accountRepository, Modifier.weight(2f)) {
                            SummaryTypeDropdown(summary, onClick = { component.onShowSummary(it) })
                        }
                    }

                    SummaryType.Expenses -> {
                        ExpensesSummaryCard(
                            component.categoryRepository,
                            component.transactionRepository,
                            Modifier.weight(2f)
                        ) {
                            SummaryTypeDropdown(summary, onClick = { component.onShowSummary(it) })
                        }
                    }

                    SummaryType.Incomes -> {
                        IncomesSummaryCard(
                            component.categoryRepository,
                            component.transactionRepository,
                            Modifier.weight(2f)
                        ) {
                            SummaryTypeDropdown(summary, onClick = { component.onShowSummary(it) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryTypeDropdown(
    value: SummaryType,
    onClick: (value: SummaryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(modifier = modifier) {
            TextButton(onClick = { expanded = true }, enabled = !expanded) {
                Text(value.toString(), style = TextStyle(fontSize = 1.1.em))
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "dropdown icon",
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.widthIn(200.dp, 450.dp)
            ) {
                SummaryType.All.forEach {
                    val isSelected = it == value
                    val style =
                        if (isSelected) MaterialTheme.typography.dropdownSelected else MaterialTheme.typography.dropdownUnselected

                    DropdownMenuItem(
                        text = { Text(it.name, style = style) },
                        onClick = {
                            onClick(it)
                            expanded = false
                        })
                }
            }
        }
    }
}


