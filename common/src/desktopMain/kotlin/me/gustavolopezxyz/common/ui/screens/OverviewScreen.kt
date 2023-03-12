/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.ext.datetime.*
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.ui.TransactionsListViewModel
import me.gustavolopezxyz.common.ui.common.*
import me.gustavolopezxyz.common.ui.core.MoneyPartitionSummary
import me.gustavolopezxyz.common.ui.theme.AppColors
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.dropdownSelected
import me.gustavolopezxyz.common.ui.theme.dropdownUnselected
import me.gustavolopezxyz.db.SelectEntriesInRange
import org.koin.java.KoinJavaComponent.inject
import kotlin.math.absoluteValue

@Composable
fun OverviewScreen(navController: NavController) {
    val transactionsListViewModel by remember {
        inject<TransactionsListViewModel>(TransactionsListViewModel::class.java)
    }
    val entryRepository by remember { inject<EntryRepository>(EntryRepository::class.java) }
    val categoryRepository by remember { inject<CategoryRepository>(CategoryRepository::class.java) }
    val accountRepository by remember { inject<AccountRepository>(AccountRepository::class.java) }

    var summary by remember { mutableStateOf(SummaryType.Owned) }

    ContainerLayout {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    AppDimensions.Default.spacing.large, Alignment.CenterHorizontally
                )
            ) {
                TransactionsOverviewCard(transactionsListViewModel, Modifier.fillMaxHeight().weight(1f)) {
                    navController.navigate(Screen.EditTransaction.route(it.transactionId))
                }

                when (summary) {
                    SummaryType.Owned -> {
                        AccountPartitionSummaryCard(accountRepository, Modifier.weight(2f)) {
                            SummaryTypeDropdown(summary, onClick = { summary = it })
                        }
                    }

                    SummaryType.Expenses -> {
                        ExpensesSummaryCard(categoryRepository, entryRepository, Modifier.weight(2f)) {
                            SummaryTypeDropdown(summary, onClick = { summary = it })
                        }
                    }
                }
            }
        }
    }
}

enum class SummaryType {
    Owned,
    Expenses;

    override fun toString(): String {
        return when (this) {
            Owned -> "Owned"

            Expenses -> "Expenses"
        }
    }
}

@Composable
fun SummaryTypeDropdown(
    value: SummaryType,
    onClick: (value: SummaryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(modifier = modifier) {
            TextButton(onClick = { expanded = true }, enabled = !expanded) {
                Text(value.toString(), style = TextStyle(fontSize = 1.1.em))
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "dropdown icon",
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.widthIn(200.dp, 450.dp)
            ) {
                SummaryType.values().forEach {
                    val isSelected = it == value
                    val style =
                        if (isSelected) MaterialTheme.typography.dropdownSelected else MaterialTheme.typography.dropdownUnselected

                    DropdownMenuItem(onClick = {
                        onClick(it)
                        expanded = false
                    }) {
                        Text(it.name, style = style)
                    }
                }
            }
        }
    }
}

val expensesColors = listOfNotNull(
    Palette.Colors["red800"],
    Palette.Colors["red600"],
    Palette.Colors["red400"],
    Palette.Colors["red200"],
    Palette.Colors["orange800"],
    Palette.Colors["orange600"],
    Palette.Colors["orange400"],
    Palette.Colors["orange200"],
    Palette.Colors["yellow800"],
    Palette.Colors["yellow600"],
    Palette.Colors["yellow400"],
    Palette.Colors["yellow200"],
)

@Composable
fun ExpensesSummaryCard(
    categoryRepository: CategoryRepository,
    entryRepository: EntryRepository,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit
) {
    var month by remember { mutableStateOf(nowLocalDateTime().date) }

    val categories by categoryRepository.allAsFlow().mapToList().map { list ->
        list.map { it.toDto() }.associateBy { it.categoryId }
    }.collectAsState(emptyMap())

    var expenses by remember { mutableStateOf(emptyList<SelectEntriesInRange>()) }
    LaunchedEffect(month) {
        entryRepository.getInRangeAsFlow(month.startOfMonth().rangeToEndOfMonth()).mapToList()
            .map { list ->
                list.filter { it.amount < 0 }.sortedBy { it.amount }
            }.collect {
                expenses = it
            }
    }

    MoneyPartitionSummary(
        currencyOf("USD"),
        expenses.groupBy {
            categories.getOrDefault(
                it.transactionCategoryId,
                MissingCategory.toDto()
            ).fullname()
        }.mapValues { entry -> entry.value.sumOf { it.amount } },
        expensesColors,
        modifier
    ) {
        AppListTitle(verticalAlignment = Alignment.Top) {
            actions()

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(month.month.toString())

                IconButton(onClick = { month = month.prevMonth() }) {
                    Icon(Icons.Default.KeyboardArrowLeft, "prev month")
                }

                IconButton(onClick = { month = month.nextMonth() }) {
                    Icon(Icons.Default.KeyboardArrowRight, "next month")
                }
            }
        }
    }
}

val assetColors = Palette.Green.reversed()

@Composable
fun AccountPartitionSummaryCard(
    accountRepository: AccountRepository,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit
) {
    val assetAccounts by derivedStateOf {
        accountRepository.getByType(AccountType.InAssets).filter { it.balance.absoluteValue > 0.01 }
            .sortedByDescending { it.balance }
    }

    MoneyPartitionSummary(
        currencyOf("USD"),
        assetAccounts.associateBy({ it.name }, { it.balance }),
        assetColors,
        modifier
    ) {
        AppListTitle(verticalAlignment = Alignment.Top, content = actions)
    }
}


const val TRANSACTIONS_PAGE_SIZE = 15

@Composable
fun TransactionsOverviewCard(
    viewModel: TransactionsListViewModel,
    modifier: Modifier,
    onClickTransaction: (MoneyTransaction) -> Unit
) {
    val categoriesById by viewModel.getCategoriesMapAsFlow().collectAsState(emptyMap())

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

    val listState = rememberLazyListState()
    listState.LaunchOnBottomReachedEffect(buffer = 4) {
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
                Text(
                    categoriesById.getOrDefault(it.categoryId, MissingCategory.toDto()).fullname(),
                    overflow = TextOverflow.Ellipsis,
                )
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(it.description, overflow = TextOverflow.Ellipsis)
            }
        }

        ListItemSpacer()

        entriesByTransaction.getOrDefault(it.transactionId, emptyList()).forEach { entry ->
            AppListItem(
                modifier = Modifier.padding(start = 12.dp),
                secondaryText = {
                    Text(entry.incurredAt.toSimpleFormat())
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