/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.di

import androidx.compose.material.SnackbarHostState
import me.gustavolopezxyz.common.ConfigFactory
import me.gustavolopezxyz.common.db.DatabaseFactory
import me.gustavolopezxyz.common.services.BackupService
import me.gustavolopezxyz.common.ui.EditTransactionViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single {
        ConfigFactory.new().also {
            logger.info("App dir: ${it.dataDir}")
            logger.info("Data file: ${it.dataFilePath}")
        }
    }

    single {
        DatabaseFactory(get()).create()
    }

    single {
        SnackbarHostState()
    }

    single {
        BackupService(get())
    }

    factory {
        EditTransactionViewModel()
    }
}