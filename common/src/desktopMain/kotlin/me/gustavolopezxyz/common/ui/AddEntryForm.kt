/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.currencyOf
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.ext.currentTz
import me.gustavolopezxyz.db.Account

data class NewEntryDto(
    val description: String = "",
    val account: Account? = null,
    val amount: Double = 0.0,
    val incurred_at: LocalDate,
    val recorded_at: LocalDate = incurred_at
)

fun emptyNewEntryDto() = NewEntryDto(incurred_at = Clock.System.now().toLocalDateTime(currentTz()).date)

@Composable
fun AddEntryForm(
    accounts: List<Account>,
    onAddEntry: (NewEntryDto) -> Unit,
    actionText: String = "Add",
    extraActions: @Composable() (() -> Unit)? = null,
) {
    var newEntry by remember { mutableStateOf(emptyNewEntryDto()) }
    var amount by remember { mutableStateOf("") }

    var isAccountsDropDownExpanded by remember { mutableStateOf(false) }

    fun handleAdd() {
        onAddEntry(newEntry)
//        newEntry = emptyNewEntryDto()
    }

    // Updates
    LaunchedEffect(amount) {
        newEntry = newEntry.copy(amount = amount.toDoubleOrNull() ?: newEntry.amount)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        Text("Add an entry", style = MaterialTheme.typography.h5)

        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Field.dp)
        ) {
            AccountsDropDown(
                expanded = isAccountsDropDownExpanded,
                onExpandedChange = { isAccountsDropDownExpanded = it },
                value = newEntry.account,
                onClick = { newEntry = newEntry.copy(account = it) },
                accounts = accounts
            ) {
                Row {
                    OutlinedTextField(value = newEntry.account?.name ?: "",
                        onValueChange = {},
                        label = { Text("Account") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "dropdown icon",
                                modifier = Modifier.clickable(true) {
                                    isAccountsDropDownExpanded = !isAccountsDropDownExpanded
                                })
                        })
                }
            }

            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = newEntry.description,
                onValueChange = { newEntry = newEntry.copy(description = it) },
                label = { Text("Description") },
                placeholder = { Text("Pizza and a few beers") })

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )

            OutlinedDateTextField(modifier = Modifier.fillMaxWidth(),
                label = { Text("Date that it happened") },
                date = newEntry.incurred_at,
                onValueChange = {
                    newEntry = newEntry.copy(
                        incurred_at = it, recorded_at = when (newEntry.recorded_at) {
                            newEntry.incurred_at -> it
                            else -> newEntry.incurred_at
                        }
                    )
                })

            OutlinedDateTextField(modifier = Modifier.fillMaxWidth(),
                label = { Text("Date that it appears in the books") },
                date = newEntry.incurred_at,
                onValueChange = { newEntry = newEntry.copy(incurred_at = it) })

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                if (extraActions != null) {
                    extraActions()
                }

                Button(onClick = ::handleAdd) { Text(actionText) }

                Button(onClick = { newEntry = emptyNewEntryDto() }) { Text("Reset") }
            }
        }
    }
}

val rowPadding = PaddingValues(12.dp, 8.dp)
val rowCellPadding = PaddingValues(4.dp, 0.dp)

@Composable
fun NewEntriesListItem(entry: NewEntryDto) {
    val accountName = when (val account = entry.account) {
        null -> ""
        else -> "on ${account.name}"
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(rowPadding)
    ) {
        Text(
            entry.description,
            modifier = Modifier.weight(5f).padding(rowCellPadding),
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            accountName,
            modifier = Modifier.weight(3f).padding(rowCellPadding),
            textAlign = TextAlign.End,
            overflow = TextOverflow.Ellipsis
        )

        MoneyText(
            entry.amount,
            entry.account?.getCurrency() ?: currencyOf("USD"),
            modifier = Modifier.weight(2f).padding(rowCellPadding),
            commonStyle = TextStyle.Default.copy(textAlign = TextAlign.End)
        )
    }
}


@Composable
fun NewEntriesList(entries: List<NewEntryDto>) {
    val amounts = entries.map { it.amount }
    val byCurrency = entries.associateBy { it.account?.getCurrency() ?: currencyOf("USD") }

    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Small.dp)
    ) {
        Text("Current entries", style = MaterialTheme.typography.h5)

        if (entries.isEmpty()) {
            Text("No entries in this transaction yet")
        }

        Column {
            entries.forEach {
                NewEntriesListItem(it)
            }
        }

        byCurrency.forEach {
            Row(modifier = Modifier.fillMaxWidth().padding(rowPadding)) {
                Text(
                    "Total",
                    modifier = Modifier.weight(8f).padding(rowCellPadding),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.body1
                )

                MoneyText(amount = amounts.reduceOrNull { acc, amount -> acc + amount } ?: 0.0,
                    currency = it.key,
                    modifier = Modifier.weight(2f).padding(rowCellPadding),
                    commonStyle = TextStyle.Default.copy(textAlign = TextAlign.End),
                    valueStyle = MaterialTheme.typography.body1)
            }
        }
    }
}

@Preview
@Composable
fun AddEntryFormPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        AddEntryForm(listOf(
            Account(99, "Savings", "USD", 100.0, 0.0), Account(2, "Checking", "VES", 50.0, 0.0)
        ), {})
    }
}

@Preview
@Composable
fun NewEntriesListPreview() {
    val ac1 = Account(99, "Income", "USD", 0.0, 0.0)
    val ac2 = Account(2, "Expenses", "USD", 0.0, 0.0)

    NewEntriesList(
        listOf(
            NewEntryDto("Cash", ac1, 100.0, LocalDate(2023, 1, 13)),
            NewEntryDto("Beer", ac2, -10.0, LocalDate(2023, 1, 14)),
        )
    )
}
