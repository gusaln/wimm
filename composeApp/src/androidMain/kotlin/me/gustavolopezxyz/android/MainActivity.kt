package me.gustavolopezxyz.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import me.gustavolopezxyz.common.App
import me.gustavolopezxyz.common.di.baseModule
import me.gustavolopezxyz.common.di.platformModule
import me.gustavolopezxyz.common.ui.theme.AppTheme
import org.kodein.di.DI
import org.kodein.di.DIAware

class MainActivity : ComponentActivity(), DIAware {
    override val di: DI = DI.lazy {
        import(baseModule())
        import(platformModule())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                App()
            }
        }
    }
}