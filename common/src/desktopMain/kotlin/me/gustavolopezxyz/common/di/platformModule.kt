/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.di

import androidx.compose.material3.SnackbarHostState
import me.gustavolopezxyz.common.ConfigFactory
import me.gustavolopezxyz.common.db.DatabaseFactory
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.services.BackupService
import me.gustavolopezxyz.common.ui.TransactionsListViewModel
import me.gustavolopezxyz.common.ui.screens.*
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
        NavController(Screen.Overview)
    }

    single {
        BackupService(get())
    }

    factory { (transactionId: Long) ->
        EditTransactionViewModel(transactionId)
    }

    factory { (navController: NavController) ->
        ManageAccountsViewModel(navController)
    }

    factory { (navController: NavController, accountId: Long) ->
        AccountSummaryViewModel(navController, accountId)
    }

    factory { (categoryId: Long, year: Int, month: Int) ->
        CategoriesMonthlySummaryViewModel(categoryId, year, month)
    }

    factory { (navController: NavController) ->
        ManageCategoriesViewModel(navController)
    }

    factory {
        TransactionsListViewModel()
    }

    factory {
        CreateTransactionViewModel()
    }
}