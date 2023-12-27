/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens.overviewScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Palette
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.money.currencyOf
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.ui.common.AppCheckbox
import me.gustavolopezxyz.desktop.ui.common.AppListTitle
import me.gustavolopezxyz.desktop.ui.core.MoneyPartitionEntry
import me.gustavolopezxyz.desktop.ui.core.MoneyPartitionSummary
import kotlin.math.absoluteValue


internal const val debitCategory = "d"

internal const val creditCategory = "c"

internal val balanceColorsMap = buildMap {
    this[debitCategory] = Palette.Green.toList()[4]
    this[creditCategory] = Palette.Yellow.toList()[4]
}

@Composable
fun BalancePartitionSummaryCard(
    accountRepository: AccountRepository,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit
) {
    var includeDebt by remember { mutableStateOf(true) }
    var includeEnvelopes by remember { mutableStateOf(true) }
    var includeNotLiquid by remember { mutableStateOf(true) }
    val assetAccounts by derivedStateOf {
        accountRepository.getAll()
            .filter { it.balance.absoluteValue > 0.01 }
            .filter {
                (includeDebt || it.balance > 0) && (includeEnvelopes || it.type != AccountType.Envelope) && (includeNotLiquid || it.type.isLiquid)
            }
            .sortedByDescending { it.balance }
    }

    MoneyPartitionSummary(
        currency = currencyOf("USD"),
        amounts = assetAccounts.map {
            MoneyPartitionEntry(it.name, if (it.balance < 0) creditCategory else debitCategory, it.balance)
        },
        fractionByCategory = true,
        colorSelector = { _, category -> balanceColorsMap.getOrDefault(category, Color.Unspecified) },
        modifier = modifier
    ) {
        AppListTitle(verticalAlignment = Alignment.Top) {
            actions()

            Spacer(Modifier.weight(1.0f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppCheckbox("Debt", includeDebt, onCheckedChange = { includeDebt = it })
                AppCheckbox("Not-liquid", includeNotLiquid, onCheckedChange = { includeNotLiquid = it })
                AppCheckbox("Envelopes", includeEnvelopes, onCheckedChange = { includeEnvelopes = it })
            }
        }
    }
}