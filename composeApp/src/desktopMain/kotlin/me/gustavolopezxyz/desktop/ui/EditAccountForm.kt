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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.ui.common.AppButton
import me.gustavolopezxyz.desktop.ui.common.AppTextButton
import me.gustavolopezxyz.desktop.ui.common.FormTitle


@Preview
@Composable
fun EditAccountForm(
    value: Account,
    onValueChange: (Account) -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.fieldSpacing)
    ) {
        FormTitle("Edit Account ${value.name}")

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value.name,
            onValueChange = { onValueChange(value.copy(name = it)) },
            label = { Text("Name") },
            placeholder = { Text("Awesome savings account") })

        AccountTypeDropdown(
            label = "Type",
            value = value.type,
            onSelect = { onValueChange(value.copy(type = it)) },
        )

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = value.currency,
            onValueChange = { onValueChange(value.copy(currency = it.uppercase())) },
            label = { Text("Currency") },
            placeholder = { Text("USD") })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium, Alignment.End)
        ) {
            AppButton(onClick = onEdit, "Edit")

            AppTextButton(onClick = onCancel, "Cancel")
        }
    }
}