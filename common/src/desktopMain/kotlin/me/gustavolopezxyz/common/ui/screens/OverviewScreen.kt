/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.*
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.ui.TransactionsListViewModel
import me.gustavolopezxyz.common.ui.common.*
import me.gustavolopezxyz.common.ui.theme.AppColors
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.java.KoinJavaComponent.inject

@Composable
fun OverviewScreen(navController: NavController) {
    val transactionsListViewModel by remember {
        inject<TransactionsListViewModel>(TransactionsListViewModel::class.java)
    }
    val accountRepository by remember { inject<AccountRepository>(AccountRepository::class.java) }

    ContainerLayout {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                AppDimensions.Default.spacing.large, Alignment.CenterHorizontally
            )
        ) {
            Spacer(modifier = Modifier.weight(1f))

            TransactionsOverviewCard(transactionsListViewModel, Modifier.fillMaxHeight().weight(3f)) {
                navController.navigate(Screen.EditTransaction.route(it.transactionId))
            }

            AccountsOverviewCard(accountRepository, Modifier.weight(3f).fillMaxHeight()) {
                navController.navigate(Screen.AccountSummary.route(it.accountId))
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

const val TRANSACTIONS_PAGE_SIZE = 15

@Composable
fun TransactionsOverviewCard(
    viewModel: TransactionsListViewModel,
    modifier: Modifier,
    onClickTransaction: (MoneyTransaction) -> Unit
) {
    val categoriesById by viewModel.getCategoriesAsFlow().mapToList().map { list ->
        list.map { it.toDto() }.associateBy { it.categoryId }
    }.collectAsState(emptyMap())

    val pagination = rememberLazyPaginationState<MoneyTransaction>()
    LaunchedEffect(pagination.pagesLoaded) {
        pagination.isLoading = true
        viewModel.getTransactionsAsFlow(1, pagination.itemsLoaded(TRANSACTIONS_PAGE_SIZE))
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

    val listState = rememberLazyListStateWithLoadMoreHandler(buffer = 4) {
        if (transactions.isNotEmpty() && !pagination.isLoading && pagination.itemsLoaded(TRANSACTIONS_PAGE_SIZE) < it.lastVisibleItemIndex + it.buffer + 1) {
            pagination.loadUpToPage(pagination.pagesLoaded + 1)
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
    ) {
        AppListItem(
            secondaryText = {
                Surface(color = MaterialTheme.colors.secondary, shape = MaterialTheme.shapes.small) {
                    Box(Modifier.padding(12.dp, 0.dp)) {
                        Text(
                            categoriesById.getOrDefault(it.categoryId, MissingCategory.toDto()).fullname(),
                            color = MaterialTheme.colors.onSecondary,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            },
            action = {
                IconButton(
                    onClick = { onClickTransaction(it) },
                    modifier = Modifier.wrapContentSize()
                ) {
                    Icon(Icons.Default.ArrowForward, "edit transaction", Modifier.size(16.dp))
                }
            }
        ) {
            Text(it.description, overflow = TextOverflow.Ellipsis)
        }

        ListItemSpacer()

        entriesByTransaction.getOrDefault(it.transactionId, emptyList()).forEach { entry ->
            AppListItem(
                modifier = Modifier.padding(start = 12.dp),
                action = {
                    MoneyText(entry.amount, entry.currency.toCurrency())
                }
            ) {
                Text(entry.accountName, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun AccountsOverviewCard(
    accountRepository: AccountRepository,
    modifier: Modifier,
    onAccountSelect: (Account) -> Unit
) {
    val accounts by accountRepository.allAsFlow().mapToList().collectAsState(emptyList())

    OverviewListCard(
        modifier = modifier,
        title = "Accounts",
        items = accounts,
    ) {
        AppListItem(action = {
            MoneyText(it.balance, it.getCurrency())

            IconButton(
                onClick = { onAccountSelect(it) },
                modifier = Modifier.wrapContentSize()
            ) {
                Icon(Icons.Default.ArrowForward, "edit transaction", Modifier.size(16.dp))
            }
        }) {
            Text(it.name)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> OverviewLazyListCard(
    modifier: Modifier,
    listState: LazyListState,
    items: List<T>,
    isLoading: Boolean,
    title: @Composable (() -> Unit),
    empty: @Composable (() -> Unit)? = null,
    itemContent: @Composable ((item: T) -> Unit)
) {
    Box(modifier) {
        AppCard(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
            AppLazyList(state = listState) {
                stickyHeader { title() }

                items(items) {
                    itemContent(it)

                    ListItemSpacer()

                    AppDivider(modifier = Modifier.fillMaxWidth())
                }

                item {
                    if (isLoading) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (items.isEmpty()) {
                        empty?.invoke()
                    }
                }
            }
        }
    }
}


@Composable
fun <T> OverviewListCard(
    modifier: Modifier,
    title: String,
    items: List<T>,
    empty: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    item: @Composable ((item: T) -> Unit)
) {
    val scroll = rememberScrollState()

    Box(modifier) {
        Card(modifier = Modifier.fillMaxHeight().fillMaxWidth().verticalScroll(scroll)) {
            AppList(modifier = Modifier.padding(AppDimensions.Default.cardPadding)) {
                AppListTitle(title)

                if (empty != null && items.isEmpty()) {
                    empty.invoke()
                }

                items.forEach {
                    item(it)

                    AppDivider(modifier = Modifier.fillMaxWidth())
                }

                footer?.invoke()
            }
        }

        VerticalScrollbar(
            rememberScrollbarAdapter(scroll),
            Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            style = LocalScrollbarStyle.current.copy(shape = RoundedCornerShape(100))
        )
    }
}