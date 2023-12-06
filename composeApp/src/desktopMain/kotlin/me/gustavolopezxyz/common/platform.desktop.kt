package me.gustavolopezxyz.common

actual fun getPlatformName(): String {
    return "Desktop"
}

actual val VERSION: Version get() = Version(0, 6, 0)