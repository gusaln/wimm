package me.gustavolopezxyz.common

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    val platformName = getPlatformName()

    Button(onClick = {
        text = "Hello, ${platformName}"
    }) {
        Text(text)
    }
}
