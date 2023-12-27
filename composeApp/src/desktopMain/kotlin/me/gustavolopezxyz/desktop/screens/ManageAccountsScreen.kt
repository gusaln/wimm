/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogWindow
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.UnknownAccount
import me.gustavolopezxyz.common.money.Currency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.ManageAccountsComponent
import me.gustavolopezxyz.desktop.ui.AccountsGroupedList
import me.gustavolopezxyz.desktop.ui.CreateAccountForm
import me.gustavolopezxyz.desktop.ui.EditAccountForm
import me.gustavolopezxyz.desktop.ui.common.AppButton
import me.gustavolopezxyz.desktop.ui.common.ScreenTitle
import org.kodein.di.instance

@Composable
fun ManageAccountsScreen(component: ManageAccountsComponent) {
    val scope = rememberCoroutineScope()
    val accounts by component.getAccounts().collectAsState(emptyList(), scope.coroutineContext)
    var isCreatingOpen by remember { mutableStateOf(false) }
    var accountBeingEdited by remember { mutableStateOf<Account?>(null) }
    val snackbar: SnackbarHostState by component.di.instance()


    fun createAccount(name: String, type: AccountType, currency: Currency) {
        isCreatingOpen = false

        component.createAccount(name, type, currency)

        scope.launch {
            snackbar.showSnackbar("The account was created")
        }
    }

    fun editAccount() {
        val modified = accountBeingEdited!!.copy()
        accountBeingEdited = null

        component.editAccount(modified)
        scope.launch {
            snackbar.showSnackbar("The account was modified")
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
            AppButton(onClick = { component.recomputeBalances() }, "Reload balances")
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        AccountsGroupedList(
            accounts,
            onSelect = { component.onShowAccountSummary(it.accountId) },
            onEdit = { accountBeingEdited = it.copy() }
        )
    }
}
