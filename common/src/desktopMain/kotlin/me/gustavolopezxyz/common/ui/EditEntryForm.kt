/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.UnknownAccount


@Composable
fun EditEntryForm(
    value: EditEntryDto,
    onValueChanged: (EditEntryDto) -> Unit,
    accounts: List<Account>,
    onEditEntry: (EditEntryDto) -> Unit,
    onCancel: () -> Unit,
) {
    val account by remember {
        derivedStateOf { accounts.find { it.accountId == value.accountId } ?: UnknownAccount }
    }

    var isAccountsDropDownExpanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        Text("Edit an entry", style = MaterialTheme.typography.h5)

        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Field.dp)
        ) {
            AccountDropdown(
                expanded = isAccountsDropDownExpanded,
                onExpandedChange = { isAccountsDropDownExpanded = it },
                value = account,
                onClick = { onValueChanged(value.changeAccount(it)) },
                accounts = accounts
            ) {
                Row {
                    OutlinedTextField(
                        value = account.name,
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

            OutlinedDoubleField(modifier = Modifier.fillMaxWidth(),
                value = value.amount,
                onValueChange = { onValueChanged(value.edit(amount = it)) },
                label = { Text("Amount") })

            OutlinedDateTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Date that it happened") },
                date = value.incurredAt,
                onValueChange = {
                    onValueChanged(
                        value.edit(
                            incurredAt = it, recordedAt = when (value.recordedAt) {
                                value.incurredAt -> it
                                else -> value.incurredAt
                            }
                        )
                    )
                })

            OutlinedDateTextField(modifier = Modifier.fillMaxWidth(),
                label = { Text("Date that it appears in the books") },
                date = value.recordedAt,
                onValueChange = { onValueChanged(value.edit(recordedAt = it)) })

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                Button(onClick = { onEditEntry(value) }) { Text("Edit entry") }
                TextButton(onClick = { onCancel() }) { Text("Leave unchanged") }
            }
        }
    }
}


@Preview
@Composable
fun EditEntryFormPreview() {
    val ac1 = Account(99, AccountType.Cash, "Savings", "USD", 100.0)
    val ac2 = Account(2, AccountType.Cash, "Checking", "VES", 50.0)

    Box(modifier = Modifier.padding(12.dp)) {
        EditEntryForm(
            value = EditEntryDto(
                1,
                ac1.accountId,
                ac1.name,
                ac1.currency,
                120.0,
                LocalDate(2023, 1, 13),
                LocalDate(2023, 1, 13)
            ),
            onValueChanged = {},
            accounts = listOf(ac1, ac2),
            onEditEntry = {},
            onCancel = {})
    }
}
