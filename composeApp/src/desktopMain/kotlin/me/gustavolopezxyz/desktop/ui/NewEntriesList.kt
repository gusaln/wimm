/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.NewEntryDto
import me.gustavolopezxyz.common.money.Currency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.screens.EntryError
import me.gustavolopezxyz.desktop.ui.common.AppChip
import me.gustavolopezxyz.desktop.ui.common.MoneyText
import me.gustavolopezxyz.desktop.ui.common.OutlinedDateTextField
import me.gustavolopezxyz.desktop.ui.common.OutlinedDoubleField


object EntriesListDefault {
    val rowPadding = PaddingValues(12.dp, 8.dp)
    val rowCellPadding = PaddingValues(4.dp, 0.dp)
    val iconSize = 20.dp

    const val actionsWeight = 2f
    const val contentWeight = 8f
    const val amountWeight = 2f
}

@Composable
fun TotalListItem(total: String = "Total", totalsByCurrency: Map<Currency, Double>) {
    totalsByCurrency.forEach {
        Row(
            modifier = Modifier.fillMaxWidth().padding(EntriesListDefault.rowPadding),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
        ) {
            Spacer(Modifier.weight(EntriesListDefault.actionsWeight))

            Text(
                total,
                modifier = Modifier.weight(EntriesListDefault.contentWeight)
                    .padding(EntriesListDefault.rowCellPadding),
                textAlign = TextAlign.End,
//                style = MaterialTheme.typography.body1
            )

            MoneyText(
                amount = it.value,
                currency = it.key,
                modifier = Modifier.weight(EntriesListDefault.amountWeight)
                    .padding(EntriesListDefault.rowCellPadding),
                commonStyle = TextStyle.Default.copy(textAlign = TextAlign.End)
            )
        }
    }
}

@Composable
fun NewEntriesListItem(
    accounts: List<Account>,
    entry: NewEntryDto,
    onEdit: (NewEntryDto) -> Unit,
    onDelete: (NewEntryDto) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val account by derivedStateOf { accounts.firstOrNull { it.accountId == entry.accountId } }

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
                    onSelect = {
                        onEdit(
                            entry.copy(
                                accountId = it.accountId,
                                amount = entry.amount.withCurrency(it.currency)
                            )
                        )
                    },
                    accounts = accounts,
                    modifier = Modifier.weight(1f),
                )

                OutlinedDoubleField(
                    value = entry.amount.value,
                    onValueChange = { onEdit(entry.copy(amount = entry.amount.withValue(it))) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Amount") },
                )

                OutlinedDateTextField(modifier = Modifier.weight(1f),
                    label = { Text("Date that it appears in the books") },
                    date = entry.recordedAt,
                    onValueChange = { onEdit(entry.copy(recordedAt = it)) })
            }

            //  Extra information section
            if (isExpanded) {
                OutlinedTextField(
                    value = entry.reference ?: "",
                    onValueChange = { onEdit(entry.copy(reference = it.replace('\n', ' ').trim().ifEmpty { null })) },
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

            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    "expand",
                    Modifier.size(EntriesListDefault.iconSize)
                )
            }
        }
    }
}


@Composable
fun NewEntriesList(
    accounts: List<Account>,
    entries: List<NewEntryDto>,
    entryError: EntryError?,
    onEdit: (NewEntryDto) -> Unit,
    onDelete: (NewEntryDto) -> Unit,
    name: @Composable() (() -> Unit)? = null,
    totals: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small),
    ) {
        name?.invoke() ?: Text("Entries", style = MaterialTheme.typography.headlineSmall)

        if (entries.isEmpty()) {
            Text("No new entries yet")
        }

        Column {
            entries.forEach {
                NewEntriesListItem(accounts, entry = it, onEdit = onEdit, onDelete = onDelete)
                if (entryError?.entryId == it.id) {
                    Text(entryError.message, color = Color.Red)
                }
                Divider(Modifier.fillMaxWidth())
            }
        }

        totals()
    }
}

@Preview
@Composable
fun NewEntriesListPreview() {
    val ac1 = Account(99, AccountType.Cash, "Income", "USD", 0.0)
    val ac2 = Account(2, AccountType.Cash, "Expenses", "USD", 0.0)

    NewEntriesList(
        accounts = listOf(ac1, ac2),
        entries = listOf(
            NewEntryDto(-1, ac1, 100.0, LocalDate(2023, 1, 13)),
            NewEntryDto(-2, ac2, -10.0, LocalDate(2023, 1, 14)),
        ),
        entryError = null,
        onEdit = {},
        onDelete = {}
    )
}