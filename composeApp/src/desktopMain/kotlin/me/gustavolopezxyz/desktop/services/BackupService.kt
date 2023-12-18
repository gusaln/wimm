/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.services

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.desktop.Config
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.math.max

class BackupService(private val config: Config) {
    private val canMakeBackups get() = isBackupDirValid()
    private val backupDir get() = File(config.backupDirPath)

    private val backupsToKeep get() = config.backupsToKeep.toInt()

    private fun isBackupDirValid(): Boolean {
        return (backupDir.isDirectory || backupDir.mkdirs()) && backupDir.canRead() && backupDir.canWrite()
    }

    fun run() {
        makeBackup()
        deleteExcessBackups()
    }

    fun listBackups(): List<Backup> {
        if (!canMakeBackups) {
            return emptyList()
        }

        return backupDir.walkTopDown()
            .maxDepth(1)
            .filter { it.isFile && it.name.matches(Regex("\\d+[.]\\d+[.]backup[.]db")) }
            .map {
                try {
                    Backup(it)
                } catch (e: Throwable) {
                    null
                }
            }
            .filterNotNull()
            .sortedBy { it.date }
            .toList()
    }

    fun makeBackup(): Backup? {
        val dataFile = File(config.dataFilePath)
        val backup = Backup()

        try {
            dataFile.copyTo(File(backup.path))
        } catch (ex: Throwable) {
            return null
        }

        return backup.also {
//            KoinJavaComponent.getKoin().logger.info("Backup $it created")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun deleteExcessBackups() {
        GlobalScope.launch(Dispatchers.IO) {
//            KoinJavaComponent.getKoin().logger.info("Checking excess backups")

            val backups = listBackups()

            if (backups.size < max(backupsToKeep, 1)) {
                return@launch
            }

            backups.dropLast(backupsToKeep).forEach {
                it.file.delete()

//                KoinJavaComponent.getKoin().logger.info("Backup $it deleted as excess")
            }
        }
    }


    data class Backup(val path: String, val date: LocalDateTime) {
        val file: File get() = File(path)
    }

    private fun Backup(file: File): Backup {
        val datePart = file.name.substringBefore('.')
        val date = LocalDateTime(
            datePart.substring(0, 4).toInt(),
            datePart.substring(4, 6).toInt(),
            datePart.substring(6, 8).toInt(),
            datePart.substring(8, 10).toInt(),
            datePart.substring(10, 12).toInt(),
            datePart.substring(12, 14).toInt(),
        )

        return Backup(file.absolutePath, date)
    }

    private fun Backup(): Backup {
        val date = Clock.System.now().toLocalDateTime(currentTimeZone())
        val backupPath = Path(
            config.backupDirPath,
            "%04d%02d%02d%02d%02d%02d.%d.backup.db".format(
                date.year, date.monthNumber, date.dayOfMonth, date.hour, date.minute, date.second,
                Database.Schema.version
            )
        )
        return Backup(
            backupPath.absolutePathString(),
            date = date
        )
    }
}
