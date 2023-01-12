import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.gustavolopezxyz.common.DesktopApp
import me.gustavolopezxyz.common.di.initKoin

fun main() = application {
    initKoin()

    Window(onCloseRequest = ::exitApplication, title = "WIMM - Where is my Money?") {
        DesktopApp()
    }
}
