/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.data.toEntryForTransaction
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.ui.core.RegularLayout
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

@Composable
fun DashboardScreen(navController: NavController) {
    val transactionsListViewModel by remember { inject<TransactionsListViewModel>(TransactionsListViewModel::class.java) }
    val accountSummaryViewModel by remember {
        inject<AccountSummaryViewModel>(AccountSummaryViewModel::class.java) {
            // Todo: Change this hard-coded value to a setting
            parametersOf(1)
        }
    }

    val transactions by transactionsListViewModel.getTransactions().mapToList()
        .collectAsState(emptyList())
    val entriesByTransaction by transactionsListViewModel.getEntries(transactions.map { it.transactionId }).mapToList()
        .map { list ->
            list.map { it.toEntryForTransaction() }.groupBy { it.transactionId }
        }
        .collectAsState(emptyMap())

    val categoryRepository by remember { inject<CategoryRepository>(CategoryRepository::class.java) }
    val categories by categoryRepository.allAsFlow().mapToList().map { list ->
        list.map { it.toDto() }.sortedBy { it.fullname() }
    }.collectAsState(emptyList())
    var categoryFilter by remember { mutableStateOf<CategoryWithParent?>(null) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    var isCreateOpen by remember { mutableStateOf(false) }
    if (isCreateOpen) {
        Window(
            onCloseRequest = { isCreateOpen = false },
            title = "Create a transaction",
            undecorated = true,
        ) {
            Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                CreateTransactionScreen(
                    onCreate = { isCreateOpen = false },
                    onCancel = { isCreateOpen = false }
                )
            }
        }
    }

    RegularLayout(menu = { Text("Empty real state") }) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Filters
                Row(
                    modifier = Modifier.weight(2f),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small, Alignment.Start)
                ) {
                    CategoryDropdown(
                        expanded = isCategoryDropdownExpanded,
                        onExpandedChange = { isCategoryDropdownExpanded = it },
                        value = categoryFilter,
                        onSelect = { categoryFilter = it },
                        categories = categories
                    ) {
                        Row {
                            TextField(value = categoryFilter?.name ?: "all",
                                onValueChange = {},
                                label = {
                                    Text("Category", modifier = Modifier.clickable(true) {
                                        isCategoryDropdownExpanded = !isCategoryDropdownExpanded
                                    })
                                },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = categoryFilter.let {
                                    if (it == null) {
                                        null
                                    } else {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = "clear category",
                                                modifier = Modifier.clickable(true) {
                                                    categoryFilter = null
                                                }
                                            )
                                        }
                                    }
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "dropdown category icon",
                                        modifier = Modifier.clickable(true) {
                                            isCategoryDropdownExpanded = !isCategoryDropdownExpanded
                                        }
                                    )
                                })
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small, Alignment.End)
                ) {
                    Button(onClick = { isCreateOpen = true }) { Text("Create entry") }
                }
            }

            Spacer(Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)) {
                Box(Modifier.weight(1f)) {
                    TransactionsList(transactions, entriesByTransaction) {
                        navController.navigate(
                            Screen.EditTransaction.route,
                            Screen.EditTransaction.withArguments(it.transactionId)
                        )
                    }
                }

                Box(Modifier.weight(1f)) {
                    AccountSummary(accountSummaryViewModel)
                }
            }
        }
    }
}
