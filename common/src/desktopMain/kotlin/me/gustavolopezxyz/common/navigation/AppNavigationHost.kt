/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.navigation

import androidx.compose.runtime.Composable
import me.gustavolopezxyz.common.ui.screens.*

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
            AccountSummaryScreen(navController, navController.getArgument("id")!!.toLong())
        }

        composable(Screen.EditTransaction.route) {
            EditTransactionScreen(navController, navController.getArgument("id")!!.toLong())
        }

        composable(Screen.ManageAccounts.route) {
            ManageAccountsScreen()
        }

        composable(Screen.ManageCategories.route) {
            ManageCategoriesScreen()
        }
    }.build()
}