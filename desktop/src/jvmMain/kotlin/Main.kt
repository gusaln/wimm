import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.gustavolopezxyz.common.DesktopApp
import me.gustavolopezxyz.common.di.initKoin
import me.gustavolopezxyz.common.services.BackupService
import me.gustavolopezxyz.common.ui.theme.AppTheme
import org.koin.java.KoinJavaComponent.get

fun main() = application {
    initKoin {
        printLogger()
    }.koin.logger.info("Application started")

    val backupService = get<BackupService>(BackupService::class.java)

    backupService.run()

    AppTheme(darkTheme = true) {
        Window(onCloseRequest = ::exitApplication, title = "WIMM - Where is my Money?") {
            DesktopApp()
        }
    }
}
