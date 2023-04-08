/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import me.gustavolopezxyz.common.ui.screens.*
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

sealed class Screen(val route: String) {
    object Overview : Screen("overview")

    object AccountSummary : Screen("accounts/{id}") {
        fun route(accountId: Long) = Route(this.route, mapOf(Pair("id", accountId.toString())))
    }

    object EditTransaction : Screen("transactions/{id}/edit") {
        fun route(transactionId: Long) = Route(this.route, mapOf(Pair("id", transactionId.toString())))
    }

    object ManageAccounts : Screen("manage_accounts")

    object ManageCategories : Screen("manage_categories")
}


@Composable
fun AppNavigationHost(navController: NavController) {
    NavigationHost(navController) {
        composable(Screen.Overview.route) {
            OverviewScreen(navController)
        }

        composable(Screen.AccountSummary.route) {
            val viewModel by remember {
                inject<AccountSummaryViewModel>(AccountSummaryViewModel::class.java) {
                    parametersOf(navController, navController.getArgument("id")!!.toLong())
                }
            }

            AccountSummaryScreen(viewModel)
        }

        composable(Screen.EditTransaction.route) {
            EditTransactionScreen(navController, navController.getArgument("id")!!.toLong())
        }

        composable(Screen.ManageAccounts.route) {
            val viewModel by remember {
                inject<ManageAccountsViewModel>(ManageAccountsViewModel::class.java) {
                    parametersOf(navController)
                }
            }

            ManageAccountsScreen(viewModel)
        }

        composable(Screen.ManageCategories.route) {
            ManageCategoriesScreen()
        }
    }.build()
}