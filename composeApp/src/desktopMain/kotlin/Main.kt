/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.gustavolopezxyz.common.di.initKoin
import me.gustavolopezxyz.common.ui.theme.AppTheme
import me.gustavolopezxyz.desktop.DesktopApp
import me.gustavolopezxyz.desktop.services.BackupService
import org.koin.java.KoinJavaComponent.get

fun main() = application {
    initKoin {
        printLogger()
    }.koin.logger.info("Application started")

    val backupService = get<BackupService>(BackupService::class.java)

    backupService.deleteExcessBackups()

    AppTheme(darkTheme = true) {
        Window(onCloseRequest = {
            backupService.makeBackup()
            exitApplication()
        }, title = "WIMM - Where is my Money?") {
            DesktopApp()
        }
    }
}
