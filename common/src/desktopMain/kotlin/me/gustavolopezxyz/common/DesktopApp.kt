package me.gustavolopezxyz.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import me.gustavolopezxyz.common.di.initKoin
import me.gustavolopezxyz.common.ui.AppNavigationHost
import me.gustavolopezxyz.common.ui.CreateTransactionScreen
import me.gustavolopezxyz.common.ui.Screen
import me.gustavolopezxyz.common.ui.rememberNavController
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.java.KoinJavaComponent.get

@Composable
fun DesktopApp() {
    val navController by rememberNavController(Screen.Transactions.route)
    val scaffoldState =
        rememberScaffoldState(snackbarHostState = get(SnackbarHostState::class.java))

    var isCreateOpen by remember { mutableStateOf(false) }
    if (isCreateOpen) {
        Window(
            onCloseRequest = { isCreateOpen = false },
            title = "Create a transaction",
            undecorated = true,
        ) {
            Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                CreateTransactionScreen(
                    onCreate = { isCreateOpen = false },
                    onCancel = { isCreateOpen = false }
                )
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(contentPadding = PaddingValues(AppDimensions.Default.topBarHorizontalPadding, 0.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("WIMM", style = MaterialTheme.typography.subtitle1)

                    Spacer(modifier = Modifier.width(42.dp))

                    Text(
                        "Transactions",
                        modifier = Modifier
                            .clickable { navController.navigate(Screen.Transactions.route) }
                            .padding(AppDimensions.Default.padding.medium * 1.2f, AppDimensions.Default.padding.medium),
                        style = MaterialTheme.typography.subtitle2
                    )

                    Text(
                        "Balances",
                        modifier = Modifier
                            .clickable { navController.navigate(Screen.Balances.route) }
                            .padding(AppDimensions.Default.padding.medium * 1.2f, AppDimensions.Default.padding.medium),
                        style = MaterialTheme.typography.subtitle2
                    )

                    Text(
                        "Manage accounts",
                        modifier = Modifier
                            .clickable { navController.navigate(Screen.ManageAccounts.route) }
                            .padding(AppDimensions.Default.padding.medium * 1.2f, AppDimensions.Default.padding.medium),
                        style = MaterialTheme.typography.subtitle2
                    )

                    Text(
                        "Manage categories",
                        modifier = Modifier
                            .clickable { navController.navigate(Screen.ManageCategories.route) }
                            .padding(AppDimensions.Default.padding.medium * 1.2f, AppDimensions.Default.padding.medium),
                        style = MaterialTheme.typography.subtitle2
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(onClick = { isCreateOpen = true }) {
                        Icon(Icons.Default.Add, "create transaction")

                        Spacer(modifier = Modifier.width(AppDimensions.Default.padding.large))

                        Text("Create transaction")
                    }
                }
            }
        },
    ) {
        AppNavigationHost(navController)
    }
}

@Preview
@Composable
fun DesktopAppPreview() {
    initKoin()

    DesktopApp()
}