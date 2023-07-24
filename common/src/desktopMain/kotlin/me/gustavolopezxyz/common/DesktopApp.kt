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
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.di.initKoin
import me.gustavolopezxyz.common.navigation.AppNavigationHost
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.navigation.rememberNavController
import me.gustavolopezxyz.common.ui.common.AppTextButton
import me.gustavolopezxyz.common.ui.screens.CreateTransactionScreen
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.java.KoinJavaComponent.get

@Composable
fun DesktopApp() {
    val navController by rememberNavController(get(NavController::class.java))
    val scaffoldState = rememberScaffoldState(snackbarHostState = get(SnackbarHostState::class.java))
    val scope = rememberCoroutineScope()

    var isCreateOpen by remember { mutableStateOf(false) }
    if (isCreateOpen) {
        Window(
            onCloseRequest = { isCreateOpen = false },
            title = "Create a transaction",
            undecorated = true,
        ) {
            Scaffold(scaffoldState = scaffoldState) {
                Card(Modifier.fillMaxSize(), shape = RoundedCornerShape(0)) {
                    CreateTransactionScreen(
                        onCreate = {
                            isCreateOpen = false
                            scope.launch { scaffoldState.snackbarHostState.showSnackbar("Transaction recorded") }
                        },
                        onCancel = { isCreateOpen = false }
                    )
                }
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

                    NavigationLink(navController, Screen.Overview.route, "Overview")

                    NavigationLink(navController, Screen.ManageAccounts.route, "Manage accounts")

                    NavigationLink(navController, Screen.ManageCategories.route, "Manage categories")

                    Spacer(modifier = Modifier.weight(1f))

                    AppTextButton(
                        onClick = { isCreateOpen = true },
                        text = "Create transaction",
                        icon = { Icon(Icons.Default.Add, "create transaction") })
                }
            }
        },
    ) {
        AppNavigationHost(navController)
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
        style = MaterialTheme.typography.subtitle2
    )
}

@Preview
@Composable
fun DesktopAppPreview() {
    initKoin()

    DesktopApp()
}