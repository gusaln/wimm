package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.ui.core.OutlinedDateTextField
import me.gustavolopezxyz.common.ui.core.OutlinedDoubleField


@Composable
fun ModifiedEntriesListItem(
    accounts: List<Account>,
    entry: ModifiedEntryDto,
    onEdit: (ModifiedEntryDto) -> Unit,
    onDelete: (ModifiedEntryDto) -> Unit
) {
    var isAccountsDropDownExpanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(Constants.Size.MediumDp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(EntriesListDefault.rowPadding),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.SmallDp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(EntriesListDefault.rowPadding),
                horizontalArrangement = Arrangement.spacedBy(Constants.Size.SmallDp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccountDropdown(
                    expanded = isAccountsDropDownExpanded,
                    onExpandedChange = { isAccountsDropDownExpanded = it },
                    value = accounts.first { it.accountId == entry.accountId },
                    onClick = { onEdit(entry.changeAccount(it)) },
                    accounts = accounts,
                    modifier = Modifier.weight(1f),
                ) {
                    Row {
                        OutlinedTextField(
                            value = entry.accountName,
                            onValueChange = {},
                            label = {
                                Text("Account", modifier = Modifier.clickable {
                                    isAccountsDropDownExpanded = !isAccountsDropDownExpanded
                                })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "dropdown icon",
                                    modifier = Modifier.clickable {
                                        isAccountsDropDownExpanded = !isAccountsDropDownExpanded
                                    })
                            },
                            readOnly = true
                        )
                    }
                }

                OutlinedDoubleField(
                    value = entry.amount,
                    onValueChange = { onEdit(entry.edit(amount = it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Amount") },
                )

                OutlinedDateTextField(modifier = Modifier.weight(1f),
                    label = { Text("Date that it happened") },
                    date = entry.incurredAt,
                    onValueChange = {
                        onEdit(
                            entry.edit(
                                incurredAt = it, recordedAt = when (entry.recordedAt) {
                                    entry.incurredAt -> it
                                    else -> entry.recordedAt
                                }
                            )
                        )
                    })

                OutlinedDateTextField(modifier = Modifier.weight(1f),
                    label = { Text("Date that it appears in the books") },
                    date = entry.recordedAt,
                    onValueChange = { onEdit(entry.edit(recordedAt = it)) })
            }
        }

        IconButton(onClick = { onDelete(entry) }, Modifier.padding(end = 8.dp)) {
            Icon(Icons.Default.Delete, "delete new entry", Modifier.size(EntriesListDefault.iconSize))
        }
    }
}

@Composable
fun ModifiedEntriesListDeletedItem(entry: ModifiedEntryDto, onRestore: (ModifiedEntryDto) -> Unit) {
    Row(
        modifier = Modifier.background(color = Color.LightGray),
        horizontalArrangement = Arrangement.spacedBy(Constants.Size.MediumDp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(EntriesListDefault.rowPadding),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.SmallDp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(EntriesListDefault.rowPadding),
                horizontalArrangement = Arrangement.spacedBy(Constants.Size.SmallDp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = entry.accountName,
                    onValueChange = {},
                    label = { Text("Account") },
                    modifier = Modifier.weight(1f),
                    readOnly = true
                )

                OutlinedDoubleField(
                    value = entry.amount,
                    onValueChange = { },
                    modifier = Modifier.weight(1f),
                    label = { Text("Amount") },
                    readOnly = true
                )

                OutlinedDateTextField(
                    modifier = Modifier.weight(1f),
                    label = { Text("Date that it happened") },
                    date = entry.incurredAt,
                    onValueChange = {}, readOnly = true
                )

                OutlinedDateTextField(
                    modifier = Modifier.weight(1f),
                    label = { Text("Date that it appears in the books") },
                    date = entry.recordedAt,
                    onValueChange = {}, readOnly = true
                )
            }
        }

        IconButton(onClick = { onRestore(entry) }, Modifier.padding(end = 8.dp)) {
            Icon(Icons.Default.Refresh, "restore entry", Modifier.size(EntriesListDefault.iconSize))
        }
    }
}

@Composable
fun ModifiedEntriesList(
    accounts: List<Account>,
    entries: List<ModifiedEntryDto>,
    onEdit: (ModifiedEntryDto) -> Unit,
    onDelete: (ModifiedEntryDto) -> Unit,
    onRestore: (ModifiedEntryDto) -> Unit,
    name: @Composable() (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.SmallDp)
    ) {
        name?.invoke() ?: Text("Entries", style = MaterialTheme.typography.h5)

        if (entries.isEmpty()) {
            Text("No entries left")
        }

        Column {
            entries.forEach {
                if (it.toDelete) {
                    ModifiedEntriesListDeletedItem(entry = it, onRestore = onRestore)
                } else {
                    ModifiedEntriesListItem(accounts, entry = it, onEdit = onEdit, onDelete = onDelete)
                }
                Divider(Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview
@Composable
fun ModifiedEntriesListPreview() {
    val ac1 = Account(99, AccountType.Cash, "Income", "USD", 0.0)
    val ac2 = Account(2, AccountType.Cash, "Expenses", "USD", 0.0)

    ModifiedEntriesList(accounts = listOf(ac1, ac2), entries = listOf(
        ModifiedEntryDto(
            -1,
            ac1.accountId,
            ac1.name,
            ac1.currency,
            100.0,
            LocalDate(2023, 1, 13),
            LocalDate(2023, 1, 13)
        ),
        ModifiedEntryDto(
            -2,
            ac2.accountId,
            ac2.name,
            ac2.currency,
            -10.0,
            LocalDate(2023, 1, 14),
            LocalDate(2023, 1, 14),
            wasEdited = true
        ),
        ModifiedEntryDto(
            -2,
            ac2.accountId,
            ac2.name,
            ac2.currency,
            -10.0,
            LocalDate(2023, 1, 14),
            LocalDate(2023, 1, 14),
            toDelete = true
        ),
    ), onEdit = {}, onDelete = {}, onRestore = {})
}