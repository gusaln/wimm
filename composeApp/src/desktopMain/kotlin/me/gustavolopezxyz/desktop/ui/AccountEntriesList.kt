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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.EntryForAccount
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.ext.datetime.formatDateTime
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.ui.common.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccountEntriesList(
    account: Account,
    entries: List<EntryForAccount>,
    isLoading: Boolean,
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onSelectEntry: (EntryForAccount) -> Unit,
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

                entries.forEach {
                    AppListItem(
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable {
                            onSelectEntry(it)
                        },
                        verticalPadding = 20.dp,
                        secondaryText = {
                            Text(it.recordedAt.formatDateTime())
                        },
                        action = {
                            MoneyText(it.amount, account.getCurrency())
                        }
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
                            Text(it.transactionDescription)

                            if (it.reference != null) {
                                AppChip(color = MaterialTheme.colorScheme.secondary) {
                                    Text("ref: ${it.reference}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    AppDivider(modifier = Modifier.fillMaxWidth())
                }

                if (entries.isEmpty()) {
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