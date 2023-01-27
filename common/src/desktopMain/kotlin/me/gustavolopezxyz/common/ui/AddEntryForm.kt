/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType


@Composable
fun AddEntryForm(
    value: NewEntryDto,
    onValueChanged: (NewEntryDto) -> Unit,
    accounts: List<Account>,
    actions: @Composable() (() -> Unit)? = null,
) {
    var isAccountsDropDownExpanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        Text("Add an entry", style = MaterialTheme.typography.h5)

        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Field.dp)
        ) {
            AccountDropdown(
                expanded = isAccountsDropDownExpanded,
                onExpandedChange = { isAccountsDropDownExpanded = it },
                value = value.account,
                onClick = { onValueChanged(value.copy(account = it)) },
                accounts = accounts
            ) {
                Row {
                    OutlinedTextField(
                        value = value.account?.name ?: "",
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
                        })
                }
            }

            OutlinedDoubleField(modifier = Modifier.fillMaxWidth(),
                value = value.amount,
                onValueChange = { onValueChanged(value.copy(amount = it)) },
                label = { Text("Amount") })

            OutlinedDateTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Date that it happened") },
                date = value.incurredAt,
                onValueChange = {
                    onValueChanged(
                        value.copy(
                            incurredAt = it, recordedAt = when (value.recordedAt) {
                                value.incurredAt -> it
                                else -> value.incurredAt
                            }
                        )
                    )
                })

            OutlinedDateTextField(modifier = Modifier.fillMaxWidth(),
                label = { Text("Date that it appears in the books") },
                date = value.incurredAt,
                onValueChange = { onValueChanged(value.copy(incurredAt = it)) })

            actions?.invoke()
        }
    }
}


@Preview
@Composable
fun AddEntryFormPreview() {
    Box(modifier = Modifier.padding(12.dp)) {
        AddEntryForm(
            value = makeEmptyNewEntryDto(), onValueChanged = {}, accounts = listOf(
                Account(99, AccountType.Cash, "Savings", "USD", 100.0),
                Account(2, AccountType.Cash, "Checking", "VES", 50.0)
            )
        )
    }
}
