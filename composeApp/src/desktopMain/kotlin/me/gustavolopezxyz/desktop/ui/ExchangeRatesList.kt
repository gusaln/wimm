/*
 * Copyright (c) 2024. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.gustavolopezxyz.common.data.ExchangeRate
import me.gustavolopezxyz.common.ext.datetime.formatDate
import me.gustavolopezxyz.common.money.MoneyAmountFormat
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.ui.common.AppCard
import me.gustavolopezxyz.desktop.ui.common.AppCardTitle
import me.gustavolopezxyz.desktop.ui.common.AppListItem
import me.gustavolopezxyz.desktop.ui.common.SimplePaginationControl

@Composable
fun ExchangeRatesList(
    exchangeRates: List<ExchangeRate>,
    isLoading: Boolean,
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier,
) {
    val onNextPageAction = rememberUpdatedState(onNextPage)
    val onPrevPageAction = rememberUpdatedState(onPrevPage)

    AppCard(modifier = modifier) {
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                AppCardTitle {
                    Text("Exchange Rates")
                }

                Spacer(Modifier.fillMaxWidth().height(AppDimensions.Default.spacing.medium))
            }

            if (exchangeRates.isEmpty()) {
                item {
                    Text("No exchange rates found", color = Color.Gray)
                }
            }

            items(exchangeRates) {
                AppListItem(Modifier, secondaryText = {
                    Text(it.effectiveSince.formatDate())
                }, action = {
                    Text(MoneyAmountFormat.format(it.rate))
                }) {
                    Text("${it.baseCurrency}/${it.counterCurrency}")
                }
            }

            item {
                SimplePaginationControl(
                    isPrevEnabled = !isFirstPage,
                    isNextEnabled = !isLastPage,
                    onPrevPage = {
                        onPrevPageAction.value.invoke()
                    },
                    onNextPage = {
                        onNextPageAction.value.invoke()
                    }
                )
            }
        }
    }
}