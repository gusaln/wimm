/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.di

import androidx.compose.material.SnackbarHostState
import me.gustavolopezxyz.common.ConfigFactory
import me.gustavolopezxyz.common.db.DatabaseFactory
import me.gustavolopezxyz.common.services.BackupService
import me.gustavolopezxyz.common.ui.TransactionsListViewModel
import me.gustavolopezxyz.common.ui.screens.AccountSummaryViewModel
import me.gustavolopezxyz.common.ui.screens.CreateTransactionViewModel
import me.gustavolopezxyz.common.ui.screens.EditTransactionViewModel
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

    factory { (transactionId: Long) ->
        EditTransactionViewModel(transactionId)
    }

    factory { (accountId: Long) ->
        AccountSummaryViewModel(accountId)
    }

    factory {
        TransactionsListViewModel()
    }

    factory {
        CreateTransactionViewModel()
    }
}