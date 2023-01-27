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
import me.gustavolopezxyz.common.data.Account

@Preview
@Composable
fun EditAccountForm(
    value: Account,
    onValueChange: (Account) -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit = {}
) {
    var isTypeDropDownExpanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Field.dp)
    ) {
        FormTitle("Edit Account ${value.name}")

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value.name,
            onValueChange = { onValueChange(value.copy(name = it)) },
            label = { Text("Name") },
            placeholder = { Text("Awesome savings account") })

        AccountTypeDropdown(
            expanded = isTypeDropDownExpanded,
            onExpandedChange = { isTypeDropDownExpanded = it },
            value = value.type,
            onClick = { onValueChange(value.copy(type = it)) },
        ) {
            Row {
                OutlinedTextField(value = value.type.name,
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
            value = value.currency,
            onValueChange = { onValueChange(value.copy(currency = it.uppercase())) },
            label = { Text("Currency") },
            placeholder = { Text("USD") })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp, Alignment.End)
        ) {
            Button(onClick = onEdit) {
                Text("Edit")
            }

            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}