/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.squareup.sqldelight.runtime.coroutines.mapToList
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.ui.core.MenuLayout
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

@Composable
fun BalancesScreen(navController: NavController) {
    val accountRepository by remember { inject<AccountRepository>(AccountRepository::class.java) }
    val accounts by accountRepository.allAsFlow().mapToList().collectAsState(emptyList())

    MenuLayout(menu = { Text("Empty real state") }) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)) {
            items(accounts) {
                val viewModel by remember {
                    inject<AccountSummaryViewModel>(AccountSummaryViewModel::class.java) {
                        parametersOf(it.accountId)
                    }
                }

                AccountSummary(viewModel)
            }
        }
    }
}
