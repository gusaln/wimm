/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens.overviewScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.ext.datetime.formatDate
import me.gustavolopezxyz.common.ext.datetime.formatDateTime
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.desktop.ui.common.*

const val TRANSACTIONS_PAGE_SIZE = 15

@Composable
fun TransactionsOverviewCard(
    listState: LazyListState,
    categoriesById: Map<Long, CategoryWithParent>,
    transactions: List<MoneyTransaction>,
    entriesByTransaction: Map<Long, List<EntryForTransaction>>,
    isLoading: Boolean,
    onEditTransaction: (transaction: MoneyTransaction) -> Unit,
    onDuplicateTransaction: (transaction: MoneyTransaction, entries: List<EntryForTransaction>) -> Unit,
    modifier: Modifier,
) {
    OverviewLazyListCard(
        modifier = modifier,
        listState = listState,
        items = transactions,
        isLoading = isLoading,
        title = {
            AppListTitle("Transactions", Modifier.fillMaxWidth())
        },
        empty = {
            Row(horizontalArrangement = Arrangement.Center) {
                Text("There are no transactions")
            }
        }
    ) { transaction ->
        val category by derivedStateOf { categoriesById.getOrDefault(transaction.categoryId, MissingCategory.toDto()) }
        val entries by derivedStateOf { entriesByTransaction.getOrDefault(transaction.transactionId, emptyList()) }

        AppListItem(
            secondaryText = {
                Row {
                    Text(
                        "${transaction.incurredAt.formatDate()} - ${category.fullname()}",
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(Modifier.width(16.dp))
                }
            },
            action = {
                Row {
                    IconButton(
                        onClick = { onEditTransaction(transaction) },
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Icon(Icons.Default.Edit, "edit transaction", Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = { onDuplicateTransaction(transaction, entries) },
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Icon(Icons.Default.ContentCopy, "duplicate transaction", Modifier.size(16.dp))
                    }
                }
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(transaction.description, overflow = TextOverflow.Ellipsis)
            }
        }

        ListItemSpacer()

        entries.forEach { entry ->
            AppListItem(
                modifier = Modifier.padding(start = 12.dp),
                secondaryText = {
                    Text(entry.recordedAt.formatDateTime())
                },
                action = {
                    MoneyText(entry.amount, entry.currency.toCurrency())
                }
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
                    Text(entry.accountName, overflow = TextOverflow.Ellipsis)

                    if (entry.reference != null) {
                        AppChip(color = MaterialTheme.colorScheme.secondary) {
                            Text("ref: ${entry.reference}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}