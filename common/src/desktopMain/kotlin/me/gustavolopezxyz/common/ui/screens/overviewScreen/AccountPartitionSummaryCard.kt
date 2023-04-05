/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens.overviewScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Palette
import me.gustavolopezxyz.common.data.currencyOf
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.ui.common.AppCheckbox
import me.gustavolopezxyz.common.ui.common.AppListTitle
import me.gustavolopezxyz.common.ui.core.MoneyPartitionSummary
import me.gustavolopezxyz.common.ui.theme.AppDimensions

internal val assetColors = Palette.Green.reversed()

@Composable
fun AccountPartitionSummaryCard(
    accountRepository: AccountRepository,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit
) {
    var includeEnvelopes by remember { mutableStateOf(true) }
    var includeNotLiquid by remember { mutableStateOf(true) }
    val assetAccounts by derivedStateOf {
        accountRepository.getByType(AccountType.Owned)
            .filter { it.balance > 0.00 }
            .filter {
                (includeEnvelopes || it.type != AccountType.Envelope) && (includeNotLiquid || it.type.isLiquid)
            }
            .sortedByDescending { it.balance }
    }

    MoneyPartitionSummary(
        currency = currencyOf("USD"),
        amounts = assetAccounts.associateBy({ it.name }, { it.balance }),
        colorPalette = assetColors,
        modifier = modifier
    ) {
        AppListTitle(verticalAlignment = Alignment.Top) {
            actions()

            Spacer(Modifier.weight(1.0f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppCheckbox("Not-liquid", includeNotLiquid, onCheckedChange = { includeNotLiquid = it })
                AppCheckbox("Envelopes", includeEnvelopes, onCheckedChange = { includeEnvelopes = it })
            }
        }
    }
}