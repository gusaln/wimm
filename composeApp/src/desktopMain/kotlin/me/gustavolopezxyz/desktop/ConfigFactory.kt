/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop

import me.gustavolopezxyz.common.logging.logger
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute

object ConfigFactory {
    val logger by logger()

    fun new(): Config {
        getAppXdgDir()?.also {
            logger.debug("Using XDG_DATA_HOME env var to create Config")

            return Config(it)
        }

        getHomeDir()?.also {
            logger.debug("Using HOME env var to create Config")

            return Config(it)
        }

        val currentDirectory = getCurrentDir()

        logger.debug("Using current directory ($currentDirectory) to create Config")

        return Config(currentDirectory, "wimm.db")
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

    private fun getCurrentDir(): String {
        return Path(".").absolute().toString()
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
