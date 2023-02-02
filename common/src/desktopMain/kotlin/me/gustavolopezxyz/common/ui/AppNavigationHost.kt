/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.runtime.Composable

sealed class Screen(val route: String) {
    object Transactions : Screen("transactions")

    object Balances : Screen("balances")

    object EditTransaction : Screen("transactions/{id}/edit") {
        fun withArguments(id: Long): Map<String, String> = mapOf(Pair("id", id.toString()))
    }

    object ManageAccounts : Screen("manage_accounts")

    object ManageCategories : Screen("manage_categories")
}


@Composable
fun AppNavigationHost(navController: NavController) {
    NavigationHost(navController) {
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController)
        }

        composable(Screen.Balances.route) {
            BalancesScreen(navController)
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