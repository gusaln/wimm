/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.di.initKoin
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.AppNavigationHost
import me.gustavolopezxyz.desktop.navigation.NavController
import me.gustavolopezxyz.desktop.navigation.Screen
import me.gustavolopezxyz.desktop.navigation.rememberNavController
import me.gustavolopezxyz.desktop.ui.common.AppTextButton
import me.gustavolopezxyz.desktop.ui.screens.CreateTransactionScreen
import me.gustavolopezxyz.desktop.ui.screens.ImportTransactionsScreen
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesktopApp() {
    val navController by rememberNavController(get(NavController::class.java))
    val snackbarHostState = get<SnackbarHostState>(SnackbarHostState::class.java)
    val scope = rememberCoroutineScope()

    var isCreateOpen by remember { mutableStateOf(false) }
    if (isCreateOpen) {
        Window(
            onCloseRequest = { isCreateOpen = false },
            title = "Create a transaction",
            undecorated = true,
        ) {
            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
                Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                    CreateTransactionScreen(
                        onCreate = {
                            isCreateOpen = false
                            scope.launch { snackbarHostState.showSnackbar("Transaction recorded") }
                        },
                        onCancel = { isCreateOpen = false }
                    )
                }
            }
        }
    }

    var isImportingOpen by remember { mutableStateOf(false) }
    if (isImportingOpen) {
        Window(
            onCloseRequest = { isImportingOpen = false },
            title = "Sync transactions",
            alwaysOnTop = true,
        ) {
            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
                Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                    ImportTransactionsScreen(
                        onCreate = {
                            isImportingOpen = false
                            scope.launch { snackbarHostState.showSnackbar("Transactions imported") }
                        },
                        onCancel = { isImportingOpen = false }
                    )
                }
            }
        }
    }

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

                        NavigationLink(navController, Screen.Overview.route, "Overview")

                        NavigationLink(navController, Screen.ManageAccounts.route, "Manage accounts")

                        NavigationLink(navController, Screen.ManageCategories.route, "Manage categories")

                        Spacer(modifier = Modifier.weight(1f))

                        AppTextButton(
                            onClick = { isCreateOpen = true },
                            text = "Create transaction",
                            icon = { Icon(Icons.Default.Add, "create transaction") })

                        AppTextButton(
                            onClick = { isImportingOpen = true },
                            text = "Import",
                            icon = { Icon(Icons.Default.UploadFile, "import") })
                    }
                },
//                modifier = Modifier.fillMaxWidth().background(Palette.Colors["red300"]!!)
            )
        },
//        contentWindowInsets = WindowInsets(left = 64.dp),
    ) {
        Column {
            // Without this, the top of the content is invisible
            // This is the real value if you tint the topbar
            // Spacer(Modifier.height(52.dp))
            Spacer(Modifier.height(44.dp))
            AppNavigationHost(navController)
        }
    }
}

internal val NavigationLinkHorizontalPadding = 18.dp

@Composable
fun NavigationLink(navController: NavController, route: String, text: String) {
    Text(
        text,
        modifier = Modifier
            .clickable { navController.navigate(route) }
            .padding(NavigationLinkHorizontalPadding, AppDimensions.Default.padding.medium),
        style = MaterialTheme.typography.titleSmall
    )
}

@Preview
@Composable
fun DesktopAppPreview() {
    initKoin()

    DesktopApp()
}