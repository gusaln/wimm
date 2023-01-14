/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Money

@Preview
@Composable
fun CreateAccountForm(onAccountCreate: (name: String, initialBalance: Money) -> Unit) {
    var name by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("0.0") }

    fun handleCreateAccount() {
        onAccountCreate(name, Money(currency, initialBalance.toDouble()))
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Constants.Size.MEDIUM.dp)
    ) {
        Text("Create an Account", style = MaterialTheme.typography.h5)

        TextField(modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            placeholder = { Text("Awesome savings account") })

        TextField(modifier = Modifier.fillMaxWidth(),
            value = currency,
            onValueChange = { currency = it },
            label = { Text("Currency") },
            placeholder = { Text("USD") })

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = initialBalance,
            onValueChange = { initialBalance = it.toDoubleOrNull()?.toString() ?: it },
            label = { Text("Initial balance") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Constants.Size.MEDIUM.dp, Alignment.End)
        ) {
            Button(onClick = ::handleCreateAccount) {
                Text("Create")
            }

            Button(onClick = {
                name = ""
                currency = ""
                initialBalance = "0.0"
            }) {
                Text("Cancel")
            }
        }
    }
}