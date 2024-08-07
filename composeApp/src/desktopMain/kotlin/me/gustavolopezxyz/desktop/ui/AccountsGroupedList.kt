/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.AppTheme
import me.gustavolopezxyz.desktop.ui.common.MoneyText


@Composable
fun AccountsGroupedList(
    accounts: List<Account>,
    onSelect: (Account) -> Unit = {},
    onEdit: ((Account) -> Unit)? = null,
) {
    val scroll = rememberScrollState()
    val byType = accounts.groupBy { it.type }

    Column(
        modifier = Modifier.verticalScroll(scroll).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
    ) {
        byType.forEach { (type, accounts) ->
            Text(type.name)

            AccountsList(accounts.sortedBy { it.name }, onSelect, onEdit)

            Spacer(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun AccountsList(
    accounts: List<Account>,
    onSelect: (Account) -> Unit = {},
    onEdit: ((Account) -> Unit)? = null,
) {
    accounts.chunked(5).forEach { accountsChunk ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = AppDimensions.Default.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
        ) {
            accountsChunk.forEach { account ->
                AccountsListCard(account, onSelect, onEdit)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccountsListCard(
    account: Account,
    onSelect: (Account) -> Unit,
    onEdit: ((Account) -> Unit)? = null,
) {
    Card(
        modifier = Modifier.widthIn(200.dp, 350.dp).clickable { onSelect(account) }.pointerHoverIcon(
            PointerIcon.Hand
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(buildAnnotatedString {
                    withStyle(
                        MaterialTheme.typography.headlineSmall.toSpanStyle().copy(fontWeight = FontWeight.Normal)
                    ) {
                        append(account.name)
                    }

                    append(' ')

                    withStyle(MaterialTheme.typography.bodySmall.toSpanStyle().copy(color = Color.Gray)) {
                        append("[${account.type.name}]")
                    }
                })

                if (onEdit != null) {
                    IconButton(onClick = { onEdit(account) }, modifier = Modifier.size(16.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "edit account",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.fillMaxWidth())

            MoneyText(
                account.balance,
                account.currency.toCurrency(),
                modifier = Modifier.align(Alignment.End),
                commonStyle = MaterialTheme.typography.headlineMedium
            )
        }
    }
}


@Preview
@Composable
fun AccountsGroupedListPreview() {
    AppTheme {
        AccountsGroupedList(
            listOf(
                Account(1, AccountType.Cash, "Savings", "USD", 100.0),
                Account(2, AccountType.Cash, "Checking", "VES", 50.0),
            ),
            onEdit = {}
        )
    }
}