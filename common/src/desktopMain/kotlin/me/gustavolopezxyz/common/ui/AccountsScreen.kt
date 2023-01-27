/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.*
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Money
import me.gustavolopezxyz.common.db.AccountRepository
import org.koin.java.KoinJavaComponent.inject

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun AccountsScreen() {
    val accountRepository by inject<AccountRepository>(AccountRepository::class.java)
    val accounts by accountRepository.allAsFlow().mapToList().collectAsState(listOf(), Dispatchers.IO)
    val snackbar by inject<SnackbarHostState>(SnackbarHostState::class.java)

    var isCreatingOpen by remember { mutableStateOf(false) }

    fun createAccount(name: String, type: AccountType, initialBalance: Money) {
        isCreatingOpen = false

        GlobalScope.launch(Dispatchers.IO) {
            accountRepository.create(type, name, initialBalance)

            snackbar.showSnackbar("Account created")
        }
    }

    if (isCreatingOpen) {
        Dialog(onCloseRequest = { isCreatingOpen = false }, title = "Create an account") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(Constants.Size.Medium.dp)) {
                    CreateAccountForm(::createAccount, onCancel = { isCreatingOpen = false })
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(Constants.Size.Large.dp),
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Large.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScreenTitle("Accounts")

            Button(onClick = { isCreatingOpen = !isCreatingOpen }) {
                Text("Create account")
            }
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        AccountsList(accounts)
    }
}