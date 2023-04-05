package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.NewEntryDto
import me.gustavolopezxyz.common.ui.common.MoneyText
import me.gustavolopezxyz.common.ui.common.OutlinedDateTextField
import me.gustavolopezxyz.common.ui.common.OutlinedDoubleField
import me.gustavolopezxyz.common.ui.theme.AppDimensions


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
    var isAccountsDropDownExpanded by remember { mutableStateOf(false) }

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
                    expanded = isAccountsDropDownExpanded,
                    onExpandedChange = { isAccountsDropDownExpanded = it },
                    value = entry.account,
                    onClick = { onEdit(entry.copy(account = it)) },
                    accounts = accounts,
                    modifier = Modifier.weight(1f),
                ) {
                    Row {
                        OutlinedTextField(
                            value = entry.account?.name ?: "",
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
                    onValueChange = { onEdit(entry.copy(amount = it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Amount") },
                )

                OutlinedDateTextField(modifier = Modifier.weight(1f),
                    label = { Text("Date that it appears in the books") },
                    date = entry.recordedAt,
                    onValueChange = { onEdit(entry.copy(recordedAt = it)) })
            }
        }

        IconButton(onClick = { onDelete(entry) }) {
            Icon(Icons.Default.Delete, "delete new entry", Modifier.size(EntriesListDefault.iconSize))
        }
    }
}


@Composable
fun NewEntriesList(
    accounts: List<Account>,
    entries: List<NewEntryDto>,
    onEdit: (NewEntryDto) -> Unit,
    onDelete: (NewEntryDto) -> Unit,
    name: @Composable() (() -> Unit)? = null,
    totals: @Composable() (() -> Unit) = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small),
    ) {
        name?.invoke() ?: Text("Entries", style = MaterialTheme.typography.h5)

        if (entries.isEmpty()) {
            Text("No new entries yet")
        }

        Column {
            entries.forEach {
                NewEntriesListItem(accounts, entry = it, onEdit = onEdit, onDelete = onDelete)
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

    NewEntriesList(accounts = listOf(ac1, ac2), entries = listOf(
        NewEntryDto(-1, ac1, 100.0, LocalDate(2023, 1, 13)),
        NewEntryDto(-2, ac2, -10.0, LocalDate(2023, 1, 14)),
    ), onEdit = {}, onDelete = {})
}