/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.AccountSummaryComponent
import me.gustavolopezxyz.desktop.ui.AccountEntriesList
import me.gustavolopezxyz.desktop.ui.common.AppListTitle
import me.gustavolopezxyz.desktop.ui.common.ContainerLayout
import me.gustavolopezxyz.desktop.ui.common.MoneyText
import me.gustavolopezxyz.desktop.ui.common.ScreenTitle

const val ACCOUNT_SUMMARY_PAGE_SIZE = 10

@Composable
fun AccountSummaryScreen(component: AccountSummaryComponent) {
    if (component.account == null) {
        ContainerLayout {
            Card(modifier = Modifier.padding(AppDimensions.Default.cardPadding).widthIn(100.dp, 200.dp)) {
                Text("Account not found")
            }
        }

        return
    }

    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    val page by component.page.subscribeAsState()

    val account by remember(component.accountId) { derivedStateOf { component.account!! } }
    val entries by component.getEntries(scope.coroutineContext, ACCOUNT_SUMMARY_PAGE_SIZE)
    LaunchedEffect(entries) {
        isLoading = false
    }

    ContainerLayout {
        Column(
            Modifier.fillMaxWidth(.6f),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large)
        ) {
            ScreenTitle {
                IconButton(onClick = { component.onNavigateBack() }) {
                    Icon(Icons.Default.ArrowBack, "go back")
                }

                Spacer(Modifier.width(AppDimensions.Default.padding.extraLarge))

                Text(
                    "${component.account?.name} summary",
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
                                account.balance,
                                account.getCurrency(),
                                commonStyle = MaterialTheme.typography.displaySmall
                            )
                        }
                    }
                }
            }

            AccountEntriesList(
                account = component.account!!,
                entries = entries,
                isFirstPage = page == 1,
                isLastPage = entries.size < ACCOUNT_SUMMARY_PAGE_SIZE,
                isLoading = isLoading,
                onPrevPage = { component.onNextPage() },
                onNextPage = { component.onPrevPage() },
                onSelectEntry = { component.onSelectEntry(it) }
            )
        }
    }
}
