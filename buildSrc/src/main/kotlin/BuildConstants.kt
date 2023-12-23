/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

object BuildConstants {
    object NameSpaces {
        const val group = "me.gustavolopezxyz"

        private const val base = "$group.wimm"

        object Android {
            const val app = "$base.android"
        }

        object Desktop {
            const val desktop = "$base.desktop"
        }

        object Common {
            const val common = "$base.commmon"
            const val data = "$common.data"
        }
    }

    object AndroidApp {
        const val id = NameSpaces.Android.app
        const val versionCode = 1
        const val versionName = "1.0"
    }

    object DesktopApp {
        const val packageName = "wimm"
        const val packageVersion = "0.6.0"
    }
}