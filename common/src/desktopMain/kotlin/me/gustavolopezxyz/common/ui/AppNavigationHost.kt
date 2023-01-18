/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.runtime.Composable

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")

    object CreateEntries : Screen("create_entries")

    object Accounts : Screen("accounts")
}


@Composable
fun AppNavigationHost(
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }

        composable(Screen.CreateEntries.route) {
            CreateEntriesScreen(navController)
        }

        composable(Screen.Accounts.route) {
            AccountsScreen(navController)
        }

    }.build()
}