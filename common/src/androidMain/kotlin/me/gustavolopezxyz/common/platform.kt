package me.gustavolopezxyz.common

actual fun getPlatformName(): String {
    return "Android"
}

actual val VERSION: Version
    get() = Version(0, 1, 0)