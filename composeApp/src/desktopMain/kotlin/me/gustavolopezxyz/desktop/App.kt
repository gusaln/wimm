/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.ui.theme.AppDimensions
//import me.gustavolopezxyz.desktop.navigation.ImportTransactionsComponent
import me.gustavolopezxyz.desktop.navigation.RootComponent
import me.gustavolopezxyz.desktop.screens.CreateTransactionScreen
//import me.gustavolopezxyz.desktop.screens.ImportTransactionsScreen
import me.gustavolopezxyz.desktop.services.SnackbarService
import org.kodein.di.DI
import org.kodein.di.compose.withDI
import org.kodein.di.instance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(di: DI, component: RootComponent) = withDI(di) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val snackbarService: SnackbarService by di.instance()
        snackbarService.snackbar = snackbarHostState
    }

    val createTransactionComponent by component.rememberCreateTransactionWindowComponent()
    if (createTransactionComponent != null) {
        Window(
            onCloseRequest = { component.onCloseCreateTransactionWindow() },
            title = "Create a transaction",
            undecorated = true,
        ) {
            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, contentWindowInsets = WindowInsets(24.dp)) {
                Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                    CreateTransactionScreen(
                        component = createTransactionComponent!!,
                        onCreate = {
                            component.onCloseCreateTransactionWindow()
                            scope.launch {
                                snackbarHostState.showSnackbar("Transaction created")
                            }
                        },
                        onCancel = { component.onCloseCreateTransactionWindow() })
                }
            }
        }
    }

    var isImportingOpen by remember { mutableStateOf(false) }
//    if (isImportingOpen) {
//        Window(
//            onCloseRequest = { isImportingOpen = false },
//            title = "Sync transactions",
//            alwaysOnTop = true,
//        ) {
//            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
//                Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
//                    ImportTransactionsScreen(
//                        ImportTransactionsComponent(
//                            di = di,
//                            onImportTransaction = {
//                                isImportingOpen = false
//                                scope.launch { snackbarHostState.showSnackbar("Transactions imported") }
//                            },
//                            onSyncTransactions = {},
//                            onCancel = { isImportingOpen = false }
//                        )
//                    )
//                }
//            }
//        }
//    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Makes the topbar be in the center
                            .padding(start = 4.dp, end = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("WIMM", style = MaterialTheme.typography.titleMedium)

                        Spacer(modifier = Modifier.width(42.dp))

                        NavigationLink(onNavigate = { component.onNavigateToDashboard() }, text = "Overview")

                        NavigationLink(
                            onNavigate = { component.onNavigateToManageAccounts() },
                            text = "Manage accounts"
                        )

                        NavigationLink(
                            onNavigate = { component.onNavigateToManageCategories() },
                            text = "Manage categories"
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = { component.onOpenCreateTransactionWindow() },
                            modifier = Modifier
                        ) {
                            Icon(Icons.Default.Add, "create transaction")
                        }
                        IconButton(
                            onClick = { isImportingOpen = true },
                            modifier = Modifier
                        ) {
                            Icon(Icons.Default.UploadFile, "import")
                        }
                        IconButton(
                            onClick = { component.onNavigateToManageExchangeRates() },
                            modifier = Modifier
                        ) {
                            Icon(Icons.Default.CurrencyExchange, "manage exchange rates")
                        }
                    }
                },
            )
        },
    ) {
        AppContent(component)
    }
}

internal val NavigationLinkHorizontalPadding = 18.dp

@Composable
internal fun NavigationLink(onNavigate: () -> Unit, text: String) {
    Text(
        text,
        modifier = Modifier
            .clickable { onNavigate() }
            .padding(NavigationLinkHorizontalPadding, AppDimensions.Default.padding.medium),
        style = MaterialTheme.typography.titleSmall
    )
}