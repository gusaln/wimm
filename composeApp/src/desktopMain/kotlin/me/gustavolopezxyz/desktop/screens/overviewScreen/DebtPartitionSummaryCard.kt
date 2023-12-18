/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens.overviewScreen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Palette
import me.gustavolopezxyz.common.data.currencyOf
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.desktop.ui.common.AppListTitle
import me.gustavolopezxyz.desktop.ui.core.MoneyPartitionEntry
import me.gustavolopezxyz.desktop.ui.core.MoneyPartitionSummary

internal val debtColors = Palette.Orange.reversed()

@Composable
fun DebtPartitionSummaryCard(
    accountRepository: AccountRepository, modifier: Modifier = Modifier, actions: @Composable RowScope.() -> Unit
) {
    val assetAccounts by derivedStateOf {
        accountRepository.getByType(AccountType.All).filter { it.balance < 0.00 }.sortedBy { it.balance }
    }

    MoneyPartitionSummary(
        currency = currencyOf("USD"),
        amounts = assetAccounts.map { MoneyPartitionEntry(it.name, it.balance) },
        descending = false,
        colorPalette = debtColors,
        modifier = modifier
    ) {
        AppListTitle(verticalAlignment = Alignment.Top, content = actions)
    }
}