/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.getBalance
import me.gustavolopezxyz.db.Account


@Composable
fun AccountsListCard(account: Account) {
    Card(modifier = Modifier.widthIn(200.dp, 350.dp), elevation = 4.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.Small.dp)
        ) {
            Row {
                Text(account.name, style = MaterialTheme.typography.h5)
            }

            Text(
                "${account.getBalance().currency} ${account.getBalance().value}",
                modifier = Modifier.align(Alignment.End),
                color = Color.Gray,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun AccountsList(accounts: List<Account>) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        accounts.forEach {
            AccountsListCard(it)
        }
    }
}


@Preview
@Composable
fun AccountsListPreview() {
    AccountsList(
        listOf(
            Account(1, "Savings", "USD", 100.0, 0.0),
            Account(2, "Checking", "VES", 50.0, 0.0),
        )
    )
}