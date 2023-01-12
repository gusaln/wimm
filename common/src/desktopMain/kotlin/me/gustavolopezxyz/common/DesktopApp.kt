package me.gustavolopezxyz.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import me.gustavolopezxyz.common.di.initKoin
import me.gustavolopezxyz.common.ui.AccountsView

@Composable
fun DesktopApp() {
    Column {
        Row {
            Text("MENU")
        }

        // Main content
        AccountsView()
    }

}

@Preview
@Composable
fun DesktopAppPreview() {
    initKoin()

    DesktopApp()
}