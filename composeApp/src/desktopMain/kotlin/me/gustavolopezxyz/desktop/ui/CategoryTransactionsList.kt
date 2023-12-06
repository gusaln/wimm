/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.data.CategoryWithParent
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.data.toMoneyTransaction
import me.gustavolopezxyz.common.ext.datetime.formatDateTime
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.db.SelectTransactionsInCategoryInRange
import me.gustavolopezxyz.desktop.ui.common.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CategoryTransactionsList(
    category: CategoryWithParent,
    transactions: List<SelectTransactionsInCategoryInRange>,
    isLoading: Boolean,
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onSelectEntry: (MoneyTransaction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    val onNextPageAction = rememberUpdatedState(onNextPage)
    val onPrevPageAction = rememberUpdatedState(onPrevPage)

    Card(modifier = Modifier) {
        Column(
            modifier = Modifier.padding(AppDimensions.Default.cardPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
        ) {
            AppList(modifier = Modifier.verticalScroll(scroll), verticalArrangement = Arrangement.Top) {
                AppListTitle("Entries")
                Spacer(Modifier.height(AppDimensions.Default.listSpaceBetween))

                transactions.forEach {
                    AppListItem(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable {
                            onSelectEntry(it.toMoneyTransaction())
                        },
                        verticalPadding = 20.dp,
                        secondaryText = {
                            Text(it.incurredAt.formatDateTime())
                        },
                        action = {
                            MoneyText(it.total, it.currency.toCurrency())
                        }
                    ) {
                        Text(buildAnnotatedString {
                            append(it.description)

                            if (it.categoryId != category.categoryId) {
                                withStyle(
                                    MaterialTheme.typography.bodyMedium.toSpanStyle()
                                        .copy(color = Color.Gray)
                                ) {
                                    append(" [${it.categoryName}]")
                                }
                            }
                        })
                    }

                    AppDivider(modifier = Modifier.fillMaxWidth())
                }

                if (transactions.isEmpty()) {
                    Spacer(Modifier.height(AppDimensions.Default.listSpaceBetween))
                    Text(
                        "There are no more entries",
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                if (isLoading) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                }

                Spacer(Modifier.height(AppDimensions.Default.listSpaceBetween))

                SimplePaginationControl(
                    isPrevEnabled = !isFirstPage,
                    isNextEnabled = !isLastPage,
                    onPrevPage = {
                        onPrevPageAction.value.invoke()
                        scope.launch { scroll.scrollTo(0) }
                    },
                    onNextPage = {
                        onNextPageAction.value.invoke()
                        scope.launch { scroll.scrollTo(0) }
                    }
                )
            }
        }
    }
}