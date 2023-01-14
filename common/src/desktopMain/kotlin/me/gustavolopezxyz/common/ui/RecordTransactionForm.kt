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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.db.Account

data class NewEntryDto(
    val description: String = "",
    val account: Account? = null,
    val amount: Double = 0.0,
    val incurred_at: Instant,
    val recorded_at: Instant
)


fun emptyNewEntryDto() = NewEntryDto(
    incurred_at = Clock.System.now(), recorded_at = Clock.System.now()
)

@Preview
@Composable
fun RecordTransactionForm(accounts: List<Account>, onTransactionCreate: (entries: List<NewEntryDto>) -> Unit) {
    var name by remember { mutableStateOf("") }
    val entries = remember { mutableStateListOf<NewEntryDto>() }

    Column(
        verticalArrangement = Arrangement.spacedBy(Constants.Size.MEDIUM.dp)
    ) {
        Text("Create a Transaction", style = MaterialTheme.typography.h5)
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            placeholder = { Text("Awesome savings account") })

        Spacer(modifier = Modifier.fillMaxWidth())

        CreateEntryForm(accounts) {
            entries.add(this)
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        NewEntriesList(entries)
    }
}

@Composable
fun CreateEntryForm(accounts: List<Account>, onCreate: NewEntryDto.() -> Unit) {
    var newEntry by remember { mutableStateOf(emptyNewEntryDto()) }
    var amount by remember { mutableStateOf("") }

    var isAccountsDropDownExpanded by remember { mutableStateOf(false) }

    fun handleCreate() {
        onCreate(newEntry)
        newEntry = emptyNewEntryDto()
    }

    // Updates
    LaunchedEffect(amount) {
        newEntry = newEntry.copy(amount = amount.toDoubleOrNull() ?: newEntry.amount)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Constants.Size.MEDIUM.dp)
    ) {
        Text("Add an entry", style = MaterialTheme.typography.h5)


        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.MEDIUM.dp)
        ) {
            // Container
            Box {

                // Anchor
                Row {
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

                    DropdownMenu(
                        expanded = isAccountsDropDownExpanded,
                        onDismissRequest = { isAccountsDropDownExpanded = false },
                        modifier = Modifier.widthIn(150.dp, 350.dp)
                    ) {
                        accounts.forEach {
                            val isSelected = it.id == newEntry.account?.id
                            val style = if (isSelected) {
                                MaterialTheme.typography.body1.copy(
                                    fontWeight = FontWeight.Bold, color = MaterialTheme.colors.secondary
                                )
                            } else {
                                MaterialTheme.typography.body1.copy(
                                    fontWeight = FontWeight.Normal, color = MaterialTheme.colors.onSurface
                                )
                            }

                            DropdownMenuItem(onClick = {
                                newEntry = newEntry.copy(account = it)
                                isAccountsDropDownExpanded = false
                            }) {
                                Text(buildAnnotatedString {
                                    append(it.name)
                                    append(" ")

                                    withStyle(
                                        SpanStyle(
                                            color = Color.Gray, fontSize = MaterialTheme.typography.caption.fontSize
                                        )
                                    ) {
                                        append("(${it.balance_currency})")
                                    }
                                }, style = style)
                            }
                        }
                    }
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                Button(onClick = ::handleCreate) { Text("Add") }

                Button(onClick = { newEntry = emptyNewEntryDto() }) { Text("Reset") }
            }
        }
    }
}

@Composable
fun NewEntriesListCard(entry: NewEntryDto) {
    val amount = entry.amount
    val isNegative = (amount) < 0

    val debit = if (isNegative) {
        ""
    } else {
        amount.toString()
    }
    val credit = if (isNegative) {
        amount.toString()
    } else {
        ""
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
    ) {
        Text(
            entry.description,
            modifier = Modifier.weight(5f),
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis
        )

        Text(entry.account?.name ?: "", modifier = Modifier.weight(3f), overflow = TextOverflow.Ellipsis)

        Text(debit, modifier = Modifier.weight(1f))

        Text(credit, modifier = Modifier.weight(1f), color = Color.Red)
    }
}


@Composable
fun NewEntriesList(entries: List<NewEntryDto>) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.SMALL.dp)
    ) {
        Text("Current entries", style = MaterialTheme.typography.h5)

        if (entries.isEmpty()) {
            Text("No entries in this transaction yet")
        }

        entries.forEach {
            NewEntriesListCard(it)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Subtotal", modifier = Modifier.weight(8f), textAlign = TextAlign.End)

            Text(entries
                .filter { it.amount > 0 }
                .map { it.amount }
                .reduceOrNull { acc, amount -> acc + amount }
                ?.toString() ?: "",
                modifier = Modifier.weight(1f)
            )

            Text(entries
                .filter { it.amount < 0 }
                .map { it.amount }
                .reduceOrNull { acc, amount -> acc + amount }
                ?.toString() ?: "",
                modifier = Modifier.weight(1f),
                color = Color.Red
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Total", modifier = Modifier.weight(8f), textAlign = TextAlign.End)

            Text(entries.map { it.amount }.reduceOrNull { acc, amount -> acc + amount }?.toString() ?: "",
                modifier = Modifier.weight(2f)
            )
        }
    }
}


@Preview
@Composable
fun RecordTransactionFormPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        RecordTransactionForm(listOf(
            Account(1, "Savings", "USD", 100.0, 0.0), Account(2, "Checking", "VES", 50.0, 0.0)
        ), {})
    }
}