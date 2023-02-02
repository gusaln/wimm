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
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ui.core.FormTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions

const val defaultCurrency = "USD"

@Preview
@Composable
fun CreateAccountForm(
    onCreate: (name: String, type: AccountType, currency: Currency) -> Unit,
    onCancel: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf(defaultCurrency) }
    var type by remember { mutableStateOf(AccountType.Cash) }

    fun handleCreate() {
        name = name.trimEnd()

        if (currency.isNotEmpty() || name.isNotEmpty()) onCreate(name.trimEnd(), type, currency.toCurrency())
    }

    fun handleCancel() {
        name = ""
        currency = defaultCurrency

        onCancel()
    }

    var isTypeDropDownExpanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.fieldSpacing)
    ) {
        FormTitle("Create an Account")

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it.trimStart() },
            label = { Text("Name") },
            placeholder = { Text("Awesome savings account") },
            singleLine = true
        )

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
            onValueChange = { currency = it.uppercase().trim() },
            label = { Text("Currency") },
            placeholder = { Text("USD, EUR, etc.") })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium, Alignment.End)
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