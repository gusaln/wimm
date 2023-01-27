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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.ext.toCurrency


@Composable
fun AccountsListCard(account: Account, onSelect: (Account) -> Unit) {
    Card(modifier = Modifier.widthIn(200.dp, 350.dp).clickable { onSelect(account) }, elevation = 4.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.Small.dp)
        ) {
            Row {
                Text(buildAnnotatedString {
                    withStyle(MaterialTheme.typography.h5.toSpanStyle().copy(fontWeight = FontWeight.Normal)) {
                        append(account.name)
                    }

                    append(' ')

                    withStyle(MaterialTheme.typography.caption.toSpanStyle().copy(color = Color.Gray)) {
                        append("[${account.type.name}]")
                    }
                })
            }

            Spacer(modifier = Modifier.fillMaxWidth())


            MoneyText(
                account.balance,
                account.currency.toCurrency(),
                modifier = Modifier.align(Alignment.End),
                valueStyle = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
fun AccountsList(accounts: List<Account>, onSelect: (Account) -> Unit = {}) {
    val byType = accounts.groupBy { it.type }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Large.dp)) {
        byType.forEach { entry ->
            Text(entry.key.name)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
            ) {
                entry.value.forEach {
                    AccountsListCard(it, onSelect)
                }

                if (entry.value.isEmpty()) {
                    Text("No accounts found")
                }
            }

            Spacer(modifier = Modifier.fillMaxWidth())
        }

    }
}


@Preview
@Composable
fun AccountsListPreview() {
    AccountsList(
        listOf(
            Account(1, AccountType.Cash, "Savings", "USD", 100.0),
            Account(2, AccountType.Cash, "Checking", "VES", 50.0),
        )
    )
}