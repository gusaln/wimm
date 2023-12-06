/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop

import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute

object ConfigFactory {
    fun new(): Config {
        getAppXdgDir()?.also {
            return Config(it)
        }

        getHomeDir()?.also {
            return Config(it)
        }

        return Config(Path(".").absolute().toString(), "wimm.db")
    }

    private fun getAppXdgDir(): String? {
        val xdgDataHomePath = System.getenv("XDG_DATA_HOME") ?: return null

        val xdgDir = File(xdgDataHomePath).absoluteFile
        if (!(xdgDir.isDirectory && xdgDir.canRead() && xdgDir.canWrite())) {
            return null
        }

        val xdgWimmDir = File(xdgDir, "wimm")
        if ((xdgWimmDir.isDirectory || xdgWimmDir.mkdir()) && xdgWimmDir.canRead() && xdgWimmDir.canWrite()) {
            return xdgWimmDir.absolutePath
        }

        return null
    }

    private fun getHomeDir(): String? {
        val homePath = System.getenv("HOME") ?: return null

        val homeDir = File(homePath).absoluteFile
        if (!(homeDir.isDirectory && homeDir.canRead() && homeDir.canWrite())) {
            return null
        }

        val homeWimmDir = File(homeDir, ".wimm")
        if ((homeWimmDir.isDirectory || homeDir.mkdir()) && homeWimmDir.canRead() && homeWimmDir.canWrite()) {
            return homeWimmDir.absolutePath
        }

        return null
    }
}

class Config(
    val dataDir: String,
    val dataFileName: String = "data.db",
    val backupsToKeep: UByte = 7u,
) {
    val dataFilePath get() = Path(dataDir, dataFileName).absolute().toString()
    val backupDirPath get() = Path(dataDir, "backups").absolute().toString()
}
