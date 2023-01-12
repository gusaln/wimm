/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.runtime.coroutines.mapToList
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.db.AccountRepository
import org.koin.java.KoinJavaComponent

@Preview
@Composable
fun AccountsView() {
    val accountRepository: AccountRepository by KoinJavaComponent.inject(AccountRepository::class.java)

    val accountsFlow = accountRepository.allAsFlow().mapToList()

    val accounts by accountsFlow.collectAsState(accountRepository.getAll())

    Row(
        modifier = Modifier.fillMaxWidth().padding(Constants.Size.LARGE.dp),
        horizontalArrangement = Arrangement.spacedBy(Constants.Size.LARGE.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            CreateAccountForm { name, initialBalance ->
                accountRepository.create(name, initialBalance)
            }
        }

        Box(modifier = Modifier.weight(3f)) {
            AccountsList(accounts)
        }
    }
}