/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens.overviewScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.runtime.coroutines.mapToList
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.ext.datetime.formatDate
import me.gustavolopezxyz.common.ext.datetime.formatDateTime
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ui.TransactionsListViewModel
import me.gustavolopezxyz.common.ui.common.AppListItem
import me.gustavolopezxyz.common.ui.common.AppListTitle
import me.gustavolopezxyz.common.ui.common.ListItemSpacer
import me.gustavolopezxyz.common.ui.common.MoneyText
import me.gustavolopezxyz.common.ui.theme.AppColors
import org.koin.java.KoinJavaComponent

const val TRANSACTIONS_PAGE_SIZE = 15

@Composable
fun TransactionsOverviewCard(
    viewModel: TransactionsListViewModel, modifier: Modifier, onClickTransaction: (MoneyTransaction) -> Unit
) {
    val categoriesById by viewModel.getCategoriesMapAsFlow().collectAsState(emptyMap())

    val pagination = rememberLazyPaginationState<MoneyTransaction>()
    LaunchedEffect(pagination.pagesLoaded) {
        pagination.isLoading = true
        viewModel.getTransactionsAsFlow(1, pagination.itemsLoadedCount(TRANSACTIONS_PAGE_SIZE))
            .mapToList()
            .collect {
                pagination.items = it
                pagination.isLoading = false
            }
    }

    val transactions = pagination.items
    val entriesByTransaction by derivedStateOf {
        viewModel
            .getEntries(transactions.map { it.transactionId })
            .map { it.toEntryForTransaction() }
            .groupBy { it.transactionId }
    }

    val listState = rememberLazyListState()
    listState.LaunchOnBottomReachedEffect(buffer = 4) {
        if (transactions.isNotEmpty() && !pagination.isLoading && pagination.itemsLoadedCount(TRANSACTIONS_PAGE_SIZE) < it.minimumRequiredItemsLoadedCount() + 1) {
            pagination.loadUpToPage(pagination.pagesLoaded + 1)
            KoinJavaComponent.getKoin().logger.info("[TransactionsOverviewCard] ${pagination.pagesLoaded} pages loaded.")
        }
    }

    OverviewLazyListCard(
        modifier = modifier,
        listState = listState,
        items = transactions,
        isLoading = pagination.isLoading,
        title = {
            AppListTitle("Transactions", Modifier.background(AppColors.cardBackground).fillMaxWidth())
        },
        empty = {
            Row(horizontalArrangement = Arrangement.Center) {
                Text("There are no transactions")
            }
        }
    ) { transaction ->
        AppListItem(
            secondaryText = {
                Row {

                    Text(
                        buildAnnotatedString {
                            append(transaction.incurredAt.formatDate())

                            append(" - ")

                            append(
                                categoriesById.getOrDefault(transaction.categoryId, MissingCategory.toDto()).fullname(),
                            )
                        },
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(Modifier.width(16.dp))


                }
            },
            action = {
                IconButton(
                    onClick = { onClickTransaction(transaction) },
                    modifier = Modifier.wrapContentSize()
                ) {
                    Icon(Icons.Default.ArrowForward, "edit transaction", Modifier.size(16.dp))
                }
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(transaction.description, overflow = TextOverflow.Ellipsis)
            }
        }

        ListItemSpacer()

        entriesByTransaction.getOrDefault(transaction.transactionId, emptyList()).forEach { entry ->
            AppListItem(
                modifier = Modifier.padding(start = 12.dp),
                secondaryText = {
                    Text(entry.recordedAt.formatDateTime())
                },
                action = {
                    MoneyText(entry.amount, entry.currency.toCurrency())
                }
            ) {
                Text(entry.accountName, overflow = TextOverflow.Ellipsis)
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}