/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.runtime.Composable

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")

    object EditTransaction : Screen("transactions/{id}/edit") {
        fun withArguments(id: Long): Map<String, String> = mapOf(Pair("id", id.toString()))
    }

    object Accounts : Screen("accounts")

    object Categories : Screen("categories")
}


@Composable
fun AppNavigationHost(navController: NavController) {
    NavigationHost(navController) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }

        composable(Screen.EditTransaction.route) {
            EditTransactionScreen(navController, navController.getArgument("id")!!.toLong())
        }

        composable(Screen.Accounts.route) {
            AccountsScreen()
        }

        composable(Screen.Categories.route) {
            CategoriesScreen()
        }
    }.build()
}