package me.gustavolopezxyz.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import me.gustavolopezxyz.common.di.initKoin
import me.gustavolopezxyz.common.ui.AppNavigationHost
import me.gustavolopezxyz.common.ui.Screen
import me.gustavolopezxyz.common.ui.rememberNavController

@Composable
fun DesktopApp() {
    val navController by rememberNavController(Screen.Dashboard.name)

    Column {
        Row {
            Text("MENU")
        }

        AppNavigationHost(navController)
    }

}

@Preview
@Composable
fun DesktopAppPreview() {
    initKoin()

    DesktopApp()
}