package me.gustavolopezxyz.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.di.initKoin
import me.gustavolopezxyz.common.ui.AppNavigationHost
import me.gustavolopezxyz.common.ui.Screen
import me.gustavolopezxyz.common.ui.rememberNavController
import org.koin.java.KoinJavaComponent.inject

@Composable
fun DesktopApp() {
    val navController by rememberNavController(Screen.Dashboard.route)
    val scaffoldState =
        rememberScaffoldState(snackbarHostState = inject<SnackbarHostState>(SnackbarHostState::class.java).value)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(contentPadding = PaddingValues(Constants.Size.Large.dp, 0.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("WIMM", style = MaterialTheme.typography.h4)

                    Spacer(modifier = Modifier.width((Constants.Size.Large * 2).dp))

                    Text(
                        "Dashboard",
                        modifier = Modifier
                            .clickable { navController.navigate(Screen.Dashboard.route) }
                            .padding(Constants.Size.Small.dp),
                        style = MaterialTheme.typography.h5
                    )

                    Text(
                        "Accounts",
                        modifier = Modifier
                            .clickable { navController.navigate(Screen.Accounts.route) }
                            .padding(Constants.Size.Small.dp),
                        style = MaterialTheme.typography.h5
                    )
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