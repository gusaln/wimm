/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Money

@Preview
@Composable
fun CreateAccountForm(
    onCreate: (name: String, type: AccountType, initialBalance: Money) -> Unit,
    onCancel: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(AccountType.Cash) }
    var initialBalance by remember { mutableStateOf(0.0) }

    fun handleCreate() {
        onCreate(name, type, Money(currency, initialBalance))
    }

    fun handleCancel() {
        name = ""
        currency = ""
        initialBalance = 0.0

        onCancel()
    }

    var isTypeDropDownExpanded by remember { mutableStateOf(false) }


    Column(
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Field.dp)
    ) {
        Text("Create an Account", style = MaterialTheme.typography.h5)

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            placeholder = { Text("Awesome savings account") })

        AccountTypeDropdown(
            expanded = isTypeDropDownExpanded,
            onExpandedChange = { isTypeDropDownExpanded = it },
            value = type,
            onClick = { type = it },
        ) {
            Row {
                OutlinedTextField(value = type.name,
                    onValueChange = {},
                    label = {
                        Text("Type", modifier = Modifier.clickable(true) {
                            isTypeDropDownExpanded = !isTypeDropDownExpanded
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "dropdown icon",
                            modifier = Modifier.clickable(true) {
                                isTypeDropDownExpanded = !isTypeDropDownExpanded
                            }
                        )
                    })
            }
        }

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = currency,
            onValueChange = { currency = it },
            label = { Text("Currency") },
            placeholder = { Text("USD") })

        OutlinedDoubleField(
            modifier = Modifier.fillMaxWidth(),
            value = initialBalance,
            onValueChange = { initialBalance = it },
            label = { Text("Initial balance") },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp, Alignment.End)
        ) {
            Button(onClick = ::handleCreate) {
                Text("Create")
            }

            TextButton(onClick = ::handleCancel) {
                Text("Cancel")
            }
        }
    }
}