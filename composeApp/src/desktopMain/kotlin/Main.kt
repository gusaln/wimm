/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import me.gustavolopezxyz.common.di.initDependencyInjection
import me.gustavolopezxyz.common.ui.theme.AppTheme
import me.gustavolopezxyz.desktop.App
import me.gustavolopezxyz.desktop.navigation.RootComponent
import me.gustavolopezxyz.desktop.navigation.runOnUiThread
import me.gustavolopezxyz.desktop.services.BackupService
import org.kodein.di.instance

val di = initDependencyInjection {}

@OptIn(ExperimentalDecomposeApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()

    // Always create the root component outside Compose (outside `application`) on the UI thread
    val root =
        runOnUiThread {
            RootComponent(
                di,
                componentContext = DefaultComponentContext(lifecycle = lifecycle),
            )
        }

    application {
        val windowState = rememberWindowState()

        LifecycleController(lifecycle, windowState)

        val backupService: BackupService by di.instance()

        backupService.deleteExcessBackups()

        AppTheme(darkTheme = true) {
            Window(
                state = windowState,
                onCloseRequest = {
                    backupService.makeBackup()
                    exitApplication()
                },
                title = "WIMM - Where is my Money?"
            ) {
                App(di, root)
            }
        }
    }
}
