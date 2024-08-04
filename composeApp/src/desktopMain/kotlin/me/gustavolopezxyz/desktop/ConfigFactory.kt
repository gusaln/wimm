/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop

import me.gustavolopezxyz.common.logging.logger
import me.gustavolopezxyz.common.money.Currency
import me.gustavolopezxyz.common.money.currencyOf
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute

object ConfigFactory {
    val logger by logger()

    fun new(): Config {
        val dataDir = getDataDir()

        return Config(
            dataDir = dataDir, dataFileName = getDataFileName(dataDir), currency = getCurrency()
        );
    }

    private fun getCurrency(): Currency {
        return currencyOf(
            System.getenv("WIMM_CURRENCY") ?: "USD"
        )
    }

    private fun getDataDir(): String {
        var dirPath: String? = System.getenv("WIMM_DATA_DIR")
        if (dirPath != null) {
            return File(dirPath).absoluteFile.absolutePath
        }
        dirPath = getAppXdgDir()
        if (dirPath != null) return dirPath

        dirPath = getHomeDir()
        if (dirPath != null) return dirPath

        return getCurrentDir()
    }

    private fun getAppXdgDir(): String? {
        val xdgDataHomePath = System.getenv("XDG_DATA_HOME") ?: return null

        val xdgDir = File(xdgDataHomePath).absoluteFile
        if (!(xdgDir.isDirectory && xdgDir.canRead() && xdgDir.canWrite())) {
            return null
        }

        val wimmDir = File(xdgDir, "wimm")
        if ((wimmDir.isDirectory || wimmDir.mkdir()) && wimmDir.canRead() && wimmDir.canWrite()) {
            return wimmDir.absolutePath
        }

        return null
    }

    private fun getHomeDir(): String? {
        val homePath =
            System.getProperty("user.home") ?: System.getenv("USERPROFILE") ?: System.getenv("HOME") ?: return null

        val homeDir = File(homePath).absoluteFile
        if (!(homeDir.isDirectory && homeDir.canRead() && homeDir.canWrite())) {
            return null
        }

        val wimmDir = File(homeDir, ".wimm")
        if ((wimmDir.isDirectory || wimmDir.mkdir()) && wimmDir.canRead() && wimmDir.canWrite()) {
            return wimmDir.absolutePath
        }

        return null
    }

    private fun getCurrentDir(): String {
        return Path(".").absolute().toString()
    }

    private fun getDataFileName(dataDir: String): String {
        val name: String? = System.getenv("WIMM_DATA_FILENAME")
        if (name != null) {
            return name
        }

        if (dataDir == getCurrentDir()) {
            return "wimm.db"
        }

        return "data.db"
    }
}

class Config(
    val dataDir: String,
    val dataFileName: String = "data.db",
    val backupsToKeep: UByte = 7u,
    val currency: Currency = currencyOf("USD"),
) {
    val dataFilePath get() = Path(dataDir, dataFileName).absolute().toString()
    val backupDirPath get() = Path(dataDir, "backups").absolute().toString()
}
