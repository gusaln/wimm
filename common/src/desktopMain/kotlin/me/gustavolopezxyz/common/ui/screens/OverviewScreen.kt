/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.ui.TransactionsListViewModel
import me.gustavolopezxyz.common.ui.common.ContainerLayout
import me.gustavolopezxyz.common.ui.screens.overviewScreen.DebtPartitionSummaryCard
import me.gustavolopezxyz.common.ui.screens.overviewScreen.ExpensesSummaryCard
import me.gustavolopezxyz.common.ui.screens.overviewScreen.OwnedPartitionSummaryCard
import me.gustavolopezxyz.common.ui.screens.overviewScreen.TransactionsOverviewCard
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.dropdownSelected
import me.gustavolopezxyz.common.ui.theme.dropdownUnselected
import org.koin.java.KoinJavaComponent.inject

@Composable
fun OverviewScreen(navController: NavController) {
    val transactionsListViewModel by remember {
        inject<TransactionsListViewModel>(TransactionsListViewModel::class.java)
    }
    val entryRepository by remember { inject<EntryRepository>(EntryRepository::class.java) }
    val categoryRepository by remember { inject<CategoryRepository>(CategoryRepository::class.java) }
    val accountRepository by remember { inject<AccountRepository>(AccountRepository::class.java) }

    var summary by remember { mutableStateOf(SummaryType.Owned) }

    ContainerLayout {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    AppDimensions.Default.spacing.large, Alignment.CenterHorizontally
                )
            ) {
                TransactionsOverviewCard(transactionsListViewModel, Modifier.fillMaxHeight().weight(1f)) {
                    navController.navigate(Screen.EditTransaction.route(it.transactionId))
                }

                when (summary) {
                    SummaryType.Owned -> {
                        OwnedPartitionSummaryCard(accountRepository, Modifier.weight(2f)) {
                            SummaryTypeDropdown(summary, onClick = { summary = it })
                        }
                    }

                    SummaryType.Debt -> {
                        DebtPartitionSummaryCard(accountRepository, Modifier.weight(2f)) {
                            SummaryTypeDropdown(summary, onClick = { summary = it })
                        }
                    }

                    SummaryType.Expenses -> {
                        ExpensesSummaryCard(categoryRepository, entryRepository, Modifier.weight(2f)) {
                            SummaryTypeDropdown(summary, onClick = { summary = it })
                        }
                    }
                }
            }
        }
    }
}

enum class SummaryType {
    Owned, Debt, Expenses;

    override fun toString(): String {
        return when (this) {
            Owned -> "Owned"

            Debt -> "Debt"

            Expenses -> "Expenses"
        }
    }

    companion object {
        val All: List<SummaryType> get() = listOf(Owned, Debt, Expenses)
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

                    DropdownMenuItem(onClick = {
                        onClick(it)
                        expanded = false
                    }) {
                        Text(it.name, style = style)
                    }
                }
            }
        }
    }
}


