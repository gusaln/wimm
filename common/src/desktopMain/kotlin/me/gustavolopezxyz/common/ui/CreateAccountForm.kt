/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Money

@Preview
@Composable
fun CreateAccountForm(onCreate: (name: String, initialBalance: Money) -> Unit, onCancel: () -> Unit = {}) {
    var name by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf(0.0) }

    fun handleCreate() {
        onCreate(name, Money(currency, initialBalance))
    }

    fun handleCancel() {
        name = ""
        currency = ""
        initialBalance = 0.0

        onCancel()
    }

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