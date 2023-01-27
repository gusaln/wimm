/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.getBalance


@Composable
fun AccountsListCard(account: Account, onSelect: (Account) -> Unit) {
    Card(modifier = Modifier.widthIn(200.dp, 350.dp).clickable { onSelect(account) }, elevation = 4.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.Small.dp)
        ) {
            Row {
                Text(buildAnnotatedString {
                    withStyle(MaterialTheme.typography.h5.toSpanStyle()) {
                        append(account.name)
                    }

                    append(' ')

                    withStyle(MaterialTheme.typography.caption.toSpanStyle().copy(color = Color.Gray)) {
                        append("[${account.type.name}]")
                    }
                })
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
fun AccountsList(accounts: List<Account>, onSelect: (Account) -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        accounts.forEach {
            AccountsListCard(it, onSelect)
        }

        if (accounts.isEmpty()) {
            Text("No accounts found")
        }
    }
}


@Preview
@Composable
fun AccountsListPreview() {
    AccountsList(
        listOf(
            Account(1, AccountType.Cash, "Savings", "USD", 100.0, 0.0),
            Account(2, AccountType.Cash, "Checking", "VES", 50.0, 0.0),
        )
    )
}