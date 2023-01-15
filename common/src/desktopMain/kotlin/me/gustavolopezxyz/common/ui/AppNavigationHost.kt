/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.runtime.Composable

enum class Screen {
    Dashboard,

    CreateEntries,

    Accounts
}


@Composable
fun AppNavigationHost(
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.Dashboard.name) {
            DashboardScreen(navController)
        }

        composable(Screen.CreateEntries.name) {
            CreateEntriesScreen(navController)
        }

        composable(Screen.Accounts.name) {
            AccountsScreen(navController)
        }

    }.build()
}