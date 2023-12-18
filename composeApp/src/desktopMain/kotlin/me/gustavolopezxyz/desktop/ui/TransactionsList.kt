/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import me.gustavolopezxyz.common.data.EntryForTransaction
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.ext.datetime.formatDateTime
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.ui.common.AppDivider
import me.gustavolopezxyz.desktop.ui.common.CardTitle
import me.gustavolopezxyz.desktop.ui.common.MoneyText

@Composable
fun TransactionsList(
    transactions: List<MoneyTransaction>,
    entriesByTransaction: Map<Long, List<EntryForTransaction>>,
    onSelect: (MoneyTransaction) -> Unit
) {
    val scroll = rememberScrollState()

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large, alignment = Alignment.Top)
        ) {
            transactions.forEach { transaction ->
                TransactionEntriesList(
                    transaction,
                    entriesByTransaction.getOrDefault(transaction.transactionId, emptyList()),
                    onSelect
                )
            }

            Spacer(Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionEntriesList(
    transaction: MoneyTransaction,
    entries: List<EntryForTransaction>,
    onSelect: (MoneyTransaction) -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.padding(AppDimensions.Default.cardPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
        ) {
            CardTitle(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)
            ) {
                Text(
                    transaction.description,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                )

                AssistChip(
                    onClick = {},
                    label = {
                        Text("category", style = MaterialTheme.typography.bodySmall)
                    },
                    modifier = Modifier.wrapContentSize(),
                    colors = AssistChipDefaults.assistChipColors(Color.Magenta.copy(.5f)),
                    shape = MaterialTheme.shapes.small
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { onSelect(transaction) }) {
                    Icon(Icons.Default.ArrowForward, "edit transaction")
                }
            }

            AppDivider()

            entries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Description and date
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)) {
                        Text(entry.accountName)

                        Text(
                            entry.recordedAt.formatDateTime(),
                            color = Color.Gray,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    }

                    // Amount
                    MoneyText(entry.amount, entry.currency.toCurrency())
                }
            }
        }
    }
}