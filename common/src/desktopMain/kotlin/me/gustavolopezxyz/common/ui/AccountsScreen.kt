/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.UnknownAccount
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.ui.core.ScreenTitle
import org.koin.java.KoinJavaComponent.inject

@Composable
fun AccountsScreen() {
    val accountRepository by inject<AccountRepository>(AccountRepository::class.java)
    val accounts by accountRepository.allAsFlow().mapToList().collectAsState(listOf(), Dispatchers.IO)
    val snackbar by inject<SnackbarHostState>(SnackbarHostState::class.java)

    val scope = rememberCoroutineScope()

    var isCreatingOpen by remember { mutableStateOf(false) }

    var editing by remember { mutableStateOf<Account?>(null) }

    fun createAccount(name: String, type: AccountType, currency: Currency) {
        isCreatingOpen = false

        scope.launch(Dispatchers.IO) {
            accountRepository.create(type, name, currency)

            snackbar.showSnackbar("The account was created")
        }
    }

    fun editAccount() {
        val modified = editing!!.copy()
        editing = null

        scope.launch(Dispatchers.IO) {
            accountRepository.update(accounts.find { it.accountId == modified.accountId }!!, modified)

            snackbar.showSnackbar("The account was modified")
        }
    }

    if (isCreatingOpen) {
        Dialog(onCloseRequest = { isCreatingOpen = false }, title = "Create an Account") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(Constants.Size.Medium.dp)) {
                    CreateAccountForm(::createAccount, onCancel = { isCreatingOpen = false })
                }
            }
        }
    }

    if (editing != null) {
        Dialog(onCloseRequest = { editing = null }, title = "Edit an Account") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(Constants.Size.Medium.dp)) {
                    EditAccountForm(
                        value = editing ?: UnknownAccount,
                        onValueChange = { editing = it },
                        onEdit = ::editAccount,
                        onCancel = { editing = null }
                    )
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

        AccountsList(accounts) { editing = it.copy() }
    }
}