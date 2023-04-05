/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.EntryForAccount
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.ext.datetime.formatDateTime
import me.gustavolopezxyz.common.ui.common.*
import me.gustavolopezxyz.common.ui.theme.AppDimensions

@Composable
fun AccountEntriesList(
    account: Account,
    entries: List<EntryForAccount>,
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Card(modifier = Modifier) {
        Column(
            modifier = Modifier.padding(AppDimensions.Default.cardPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
        ) {
            AppList {
                AppListTitle("Entries")

                entries.forEach {
                    AppListItem(
                        secondaryText = {
                            Text(it.recordedAt.formatDateTime())
                        },
                        action = {
                            MoneyText(it.amount, account.getCurrency())
                        }
                    ) {
                        Text(it.transactionDescription)
                    }

                    AppDivider(modifier = Modifier.fillMaxWidth())
                }

                if (entries.isEmpty()) {
                    Text(
                        "There no more entries",
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            SimplePaginationControl(
                isPrevEnabled = !isFirstPage,
                isNextEnabled = !isLastPage,
                onPrevPage = onPrevPage,
                onNextPage = onNextPage
            )
        }
    }
}