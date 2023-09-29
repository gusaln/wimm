/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.data.toEntryForTransaction
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.ui.CategoryDropdown
import me.gustavolopezxyz.common.ui.TransactionsList
import me.gustavolopezxyz.common.ui.TransactionsListViewModel
import me.gustavolopezxyz.common.ui.common.MenuLayout
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.java.KoinJavaComponent.inject

@Composable
fun TransactionsScreen(navController: NavController) {
    val transactionsListViewModel by remember { inject<TransactionsListViewModel>(TransactionsListViewModel::class.java) }

    var page by remember { mutableStateOf(1) }
    var perPage by remember { mutableStateOf(10) }
    val transactions by transactionsListViewModel.getTransactionsAsFlow(page, perPage).mapToList(Dispatchers.IO)
        .collectAsState(emptyList())
    val entriesByTransaction by transactionsListViewModel.getEntriesAsFlow(transactions.map { it.transactionId })
        .mapToList(Dispatchers.IO)
        .map { list ->
            list.map { it.toEntryForTransaction() }.groupBy { it.transactionId }
        }
        .collectAsState(emptyMap())

    val categoryRepository by remember { inject<CategoryRepository>(CategoryRepository::class.java) }
    val categories by categoryRepository.allAsFlow().mapToList(Dispatchers.IO).map { list ->
        list.map { it.toDto() }.sortedBy { it.fullname() }
    }.collectAsState(emptyList())
    var categoryFilter by remember { mutableStateOf<CategoryWithParent?>(null) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    MenuLayout(menu = { Text("Empty real state") }) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)) {
            // Filters
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small, Alignment.Start)
            ) {
                CategoryDropdown(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = it },
                    value = categoryFilter,
                    onSelect = { categoryFilter = it },
                    categories = categories
                ) {
                    Row(modifier = Modifier.weight(1f)) {
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

                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.fillMaxWidth())

            TransactionsList(transactions, entriesByTransaction) {
                navController.navigate(
                    Screen.EditTransaction.route(it.transactionId)
                )
            }
        }
    }
}
