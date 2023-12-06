/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.ui.common.AppButton
import me.gustavolopezxyz.desktop.ui.common.AppTextButton
import me.gustavolopezxyz.desktop.ui.common.FormTitle

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
            label = "Type",
            value = type,
            onSelect = { type = it },
        )

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = currency,
            onValueChange = { currency = it.uppercase().trim() },
            label = { Text("Currency") },
            placeholder = { Text("USD, EUR, etc.") })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium, Alignment.End)
        ) {
            AppButton(onClick = ::handleCreate, "Create")

            AppTextButton(onClick = ::handleCancel, "Cancel")
        }
    }
}