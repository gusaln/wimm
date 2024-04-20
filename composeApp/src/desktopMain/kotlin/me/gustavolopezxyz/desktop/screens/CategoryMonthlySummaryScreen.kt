/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.money.MissingCurrency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.CategoryMonthlySummaryComponent
import me.gustavolopezxyz.desktop.ui.CategoryTransactionsList
import me.gustavolopezxyz.desktop.ui.common.AppListTitle
import me.gustavolopezxyz.desktop.ui.common.ContainerLayout
import me.gustavolopezxyz.desktop.ui.common.MoneyText
import me.gustavolopezxyz.desktop.ui.common.ScreenTitle

@Composable
fun CategoryMonthlySummaryScreen(component: CategoryMonthlySummaryComponent) {
    if (component.category == null) {
        ContainerLayout {
            Card(modifier = Modifier.padding(AppDimensions.Default.cardPadding).widthIn(100.dp, 200.dp)) {
                Text("Category not found")
            }
        }

        return
    }

    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    val month by component.month.subscribeAsState()
    val category by remember(component.categoryId) { derivedStateOf { component.category } }
    val transactions by component.getTransactions(scope.coroutineContext)
    LaunchedEffect(transactions) {
        isLoading = false
    }

    ContainerLayout {
        Column(
            Modifier.fillMaxWidth(.6f),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
        ) {
            ScreenTitle {
                IconButton(onClick = { component.onNavigateBack() }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, "go back")
                }

                Spacer(Modifier.width(AppDimensions.Default.padding.extraLarge))

                Text(
                    "Summary of ${component.category.fullname()}: ${month.month.name}",
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
                category = category,
                transactions = transactions,
                isFirstPage = false,
                isLastPage = false,
                isLoading = isLoading,
                onPrevPage = { component.prevMonth() },
                onNextPage = { component.nextMonth() },
                onSelectEntry = { component.onSelectTransaction(it) }
            )
        }
    }
}
