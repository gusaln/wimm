/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.UnknownAccount
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.ui.AccountsList
import me.gustavolopezxyz.common.ui.CreateAccountForm
import me.gustavolopezxyz.common.ui.EditAccountForm
import me.gustavolopezxyz.common.ui.common.AppButton
import me.gustavolopezxyz.common.ui.common.ScreenTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.java.KoinJavaComponent.inject

@Composable
fun ManageAccountsScreen() {
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
            Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                    CreateAccountForm(::createAccount, onCancel = { isCreatingOpen = false })
                }
            }
        }
    }

    if (editing != null) {
        Dialog(onCloseRequest = { editing = null }, title = "Edit an Account") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
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
        modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.large),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
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