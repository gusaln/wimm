/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.di

import me.gustavolopezxyz.common.db.DatabaseFactory
import me.gustavolopezxyz.desktop.ConfigFactory
import me.gustavolopezxyz.desktop.services.BackupService
import me.gustavolopezxyz.desktop.services.SnackbarService
import org.kodein.di.DI
import org.kodein.di.bindInstance
import org.kodein.di.bindSingleton
import org.kodein.di.instance

actual fun platformModule(): DI.Module {
    return DI.Module(name = "PLATFORM") {
        bindInstance {
            ConfigFactory.new().also {
//                logger.info("App dir: ${it.dataDir}")
//                logger.info("Data file: ${it.dataFilePath}")
            }
        }

        bindSingleton {
            BackupService(instance())
        }

        bindSingleton {
            DatabaseFactory(instance()).create()
        }

        bindSingleton {
            SnackbarService()
        }
    }
}