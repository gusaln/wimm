/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogWindow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.UnknownAccount
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.ui.AccountsGroupedList
import me.gustavolopezxyz.common.ui.CreateAccountForm
import me.gustavolopezxyz.common.ui.EditAccountForm
import me.gustavolopezxyz.common.ui.common.AppButton
import me.gustavolopezxyz.common.ui.common.ScreenTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun ManageAccountsScreen(viewModel: ManageAccountsViewModel) {
    val scope = rememberCoroutineScope()
    val accounts by viewModel.getAccounts().collectAsState(emptyList(), scope.coroutineContext)
    var isCreatingOpen by remember { mutableStateOf(false) }
    var accountBeingEdited by remember { mutableStateOf<Account?>(null) }

    fun createAccount(name: String, type: AccountType, currency: Currency) {
        isCreatingOpen = false

        scope.launch(Dispatchers.IO) {
            viewModel.createAccount(name, type, currency)
        }
    }

    fun editAccount() {
        val modified = accountBeingEdited!!.copy()
        accountBeingEdited = null

        scope.launch(Dispatchers.IO) {
            viewModel.editAccount(modified)
        }
    }

    if (isCreatingOpen) {
        DialogWindow(onCloseRequest = { isCreatingOpen = false }, title = "Create an Account") {
            Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                    CreateAccountForm(::createAccount, onCancel = { isCreatingOpen = false })
                }
            }
        }
    }

    if (accountBeingEdited != null) {
        DialogWindow(onCloseRequest = { accountBeingEdited = null }, title = "Edit an Account") {
            Card(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                    EditAccountForm(
                        value = accountBeingEdited ?: UnknownAccount,
                        onValueChange = { accountBeingEdited = it },
                        onEdit = ::editAccount,
                        onCancel = { accountBeingEdited = null }
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.large),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
    ) {
//        Header
        Row(modifier = Modifier.fillMaxWidth()) {
            ScreenTitle("Accounts")

            Spacer(Modifier.weight(1f))

            AppButton(onClick = { isCreatingOpen = !isCreatingOpen }, "Create account")
            Spacer(Modifier.width(AppDimensions.Default.spacing.medium))
            AppButton(onClick = { viewModel.recomputeBalances() }, "Reload balances")
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        AccountsGroupedList(
            accounts,
            onSelect = { viewModel.navigateToAccountsSummary(it) },
            onEdit = { accountBeingEdited = it.copy() }
        )
    }
}

class ManageAccountsViewModel(private val navController: NavController) : KoinComponent {
    private val snackbar: SnackbarHostState by inject()
    private val accountRepository: AccountRepository by inject()

    fun getAccounts() = accountRepository.allAsFlow().mapToList(Dispatchers.IO)

    suspend fun createAccount(name: String, type: AccountType, currency: Currency) {
        accountRepository.create(type, name, currency)

        snackbar.showSnackbar("The account was created")
    }

    suspend fun editAccount(modified: Account) {
        accountRepository.update(modified)

        snackbar.showSnackbar("The account was modified")
    }

    fun recomputeBalances() {
        accountRepository.recomputeBalances()
    }

    fun navigateToAccountsSummary(account: Account) {
        navController.navigate(Screen.AccountSummary.route(account.accountId))
    }
}