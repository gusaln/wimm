/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.MissingAccount
import me.gustavolopezxyz.common.data.ModifiedEntryDto
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.ui.common.AppChip
import me.gustavolopezxyz.desktop.ui.common.OutlinedDateTextField
import me.gustavolopezxyz.desktop.ui.common.OutlinedDoubleField


@Composable
fun ModifiedEntriesListItem(
    accounts: List<Account>,
    entry: ModifiedEntryDto,
    onEdit: (ModifiedEntryDto) -> Unit,
    onDelete: (ModifiedEntryDto) -> Unit
) {
    val account by derivedStateOf {
        accounts.firstOrNull { it.accountId == entry.accountId } ?: MissingAccount
    }

    var expanded by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(EntriesListDefault.rowPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(EntriesListDefault.rowPadding),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccountDropdown(
                    label = "Account",
                    value = account,
                    onSelect = { onEdit(entry.changeAccount(it)) },
                    accounts = accounts,
                    modifier = Modifier.weight(1f),
                )

                OutlinedDoubleField(
                    value = entry.amountValue,
                    onValueChange = { onEdit(entry.edit(amount = it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Amount") },
                )

                OutlinedDateTextField(modifier = Modifier.weight(1f),
                    label = { Text("Date that it appears in the books") },
                    date = entry.recordedAt,
                    onValueChange = { onEdit(entry.edit(recordedAt = it)) })
            }

            //  Extra information section
            if (expanded) {
                OutlinedTextField(
                    value = entry.reference ?: "",
                    onValueChange = {
                        onEdit(entry.edit(reference = it.replace('\n', ' ').trim().ifEmpty { null }))
                    },
                    modifier = Modifier.fillMaxWidth().padding(EntriesListDefault.rowPadding),
                    label = { Text("Reference (optional)") },
                    maxLines = 1
                )
            } else if (entry.reference != null) {
                Box(modifier = Modifier.padding(EntriesListDefault.rowPadding)) {
                    AppChip(color = MaterialTheme.colorScheme.secondary) {
                        Text("ref: ${entry.reference}")
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = { onDelete(entry) }) {
                Icon(Icons.Default.Delete, "delete new entry", Modifier.size(EntriesListDefault.iconSize))
            }

            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    "expand",
                    Modifier.size(EntriesListDefault.iconSize)
                )
            }
        }
    }
}

@Composable
fun ModifiedEntriesListDeletedItem(entry: ModifiedEntryDto, onRestore: (ModifiedEntryDto) -> Unit) {
    Row(
        modifier = Modifier.background(color = Color.LightGray),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(EntriesListDefault.rowPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(EntriesListDefault.rowPadding),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small),
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
                    value = entry.amountValue,
                    onValueChange = { },
                    modifier = Modifier.weight(1f),
                    label = { Text("Amount") },
                    readOnly = true
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)
    ) {
        name?.invoke() ?: Text("Entries", style = MaterialTheme.typography.headlineSmall)

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
            LocalDate(2023, 1, 13)
        ),
        ModifiedEntryDto(
            -2,
            ac2.accountId,
            ac2.name,
            ac2.currency,
            -10.0,
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
            toDelete = true
        ),
    ), onEdit = {}, onDelete = {}, onRestore = {})
}