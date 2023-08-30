/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDateTime
import me.gustavolopezxyz.common.data.MissingCurrency
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.datetime.*
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.ui.CategoryTransactionsList
import me.gustavolopezxyz.common.ui.common.AppListTitle
import me.gustavolopezxyz.common.ui.common.ContainerLayout
import me.gustavolopezxyz.common.ui.common.MoneyText
import me.gustavolopezxyz.common.ui.common.ScreenTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.displaySmall
import me.gustavolopezxyz.db.SelectTransactionsInCategoryInRange
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

@Composable
fun CategoriesMonthlySummaryScreen(viewModel: CategoriesMonthlySummaryViewModel) {
    if (viewModel.category == null) {
        ContainerLayout {
            Card(modifier = Modifier.padding(AppDimensions.Default.cardPadding).widthIn(100.dp, 200.dp)) {
                Text("Category not found")
            }
        }

        return
    }

    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }

    val month by remember { viewModel.month }
    val transactions by viewModel.getTransactions(scope.coroutineContext)
    LaunchedEffect(transactions) {
        isLoading = false
    }

    ContainerLayout {
        Column(
            Modifier.fillMaxWidth(.6f),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
        ) {
            ScreenTitle {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Default.ArrowBack, "go back")
                }

                Spacer(Modifier.width(AppDimensions.Default.padding.extraLarge))

                Text(
                    "Summary of ${viewModel.category.fullname()}: ${month.month.name}",
                    modifier = Modifier.weight(1f),
                )
            }

            Row {
                Card {
                    Column(Modifier.fillMaxWidth(0.75f).padding(AppDimensions.Default.cardPadding)) {
                        AppListTitle {
                            Text("Balance")
                        }

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            MoneyText(
                                transactions.sumOf { it.total },
                                transactions.firstOrNull()?.currency?.toCurrency() ?: MissingCurrency,
                                commonStyle = MaterialTheme.typography.displaySmall
                            )
                        }
                    }
                }
            }

            CategoryTransactionsList(
                category = viewModel.category,
                transactions = transactions,
                isFirstPage = false,
                isLastPage = false,
                isLoading = isLoading,
                onPrevPage = { viewModel.prevMonth() },
                onNextPage = { viewModel.nextMonth() },
                onSelectEntry = { viewModel.selectTransaction(it) }
            )
        }
    }
}

class CategoriesMonthlySummaryViewModel(val categoryId: Long) :
    KoinComponent {
    private val navController: NavController by inject()
    private val categoryRepository: CategoryRepository by inject()
    private val transactionRepository: TransactionRepository by inject()

    private val startOfPeriod = mutableStateOf(nowLocalDateTime().startOfMonth())

    private val endOfPeriod: LocalDateTime
        get() = startOfPeriod.value.endOfMonth()

    val month: MutableState<LocalDateTime> get() = startOfPeriod

    constructor(
        categoryId: Long,
        year: Int,
        month: Int
    ) : this(categoryId) {
        this.startOfPeriod.value = LocalDateTime(year, month, 1, 0, 0, 0)
    }

    val category by lazy {
        val c = categoryRepository.findById(categoryId)

        if (c.parentCategoryId != null) {
            c.toDto(categoryRepository.findById(c.parentCategoryId).name)
        } else {
            c.toDto()
        }
    }

    fun nextMonth() {
        startOfPeriod.value = startOfPeriod.value.nextMonth()
    }

    fun prevMonth() {
        startOfPeriod.value = startOfPeriod.value.prevMonth()
    }

    @Composable
    fun getTransactions(scope: CoroutineContext): State<List<SelectTransactionsInCategoryInRange>> {
        return transactionRepository.getAllForCategoryInPeriodAsFlow(categoryId, startOfPeriod.value, endOfPeriod)
            .mapToList(Dispatchers.IO)
            .collectAsState(emptyList(), scope)
    }

    fun navigateBack() {
        navController.navigateBack()
    }

    fun selectTransaction(transaction: MoneyTransaction) {
        navController.navigate(Screen.EditTransaction.route(transaction.transactionId))
    }
}