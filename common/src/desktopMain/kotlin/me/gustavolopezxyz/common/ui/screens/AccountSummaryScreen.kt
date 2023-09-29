/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.data.EntryForAccount
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.data.toEntryForAccount
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.navigation.Screen
import me.gustavolopezxyz.common.ui.AccountEntriesList
import me.gustavolopezxyz.common.ui.common.AppListTitle
import me.gustavolopezxyz.common.ui.common.ContainerLayout
import me.gustavolopezxyz.common.ui.common.MoneyText
import me.gustavolopezxyz.common.ui.common.ScreenTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

const val ACCOUNT_SUMMARY_PAGE_SIZE = 10

@Composable
fun AccountSummaryScreen(viewModel: AccountSummaryViewModel) {
    if (viewModel.account == null) {
        ContainerLayout {
            Card(modifier = Modifier.padding(AppDimensions.Default.cardPadding).widthIn(100.dp, 200.dp)) {
                Text("Account not found")
            }
        }

        return
    }

    var isLoading by remember { mutableStateOf(true) }

    val account by remember(viewModel.accountId.toString()) { derivedStateOf { viewModel.account!! } }
    var page by remember { mutableStateOf(1) }
    val entries by viewModel.getEntries(page, ACCOUNT_SUMMARY_PAGE_SIZE).mapToList(Dispatchers.IO)
        .map { list -> list.map { it.toEntryForAccount() } }
        .collectAsState(emptyList())
    LaunchedEffect(entries) {
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
                    "${viewModel.account?.name} summary",
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
                account = viewModel.account!!,
                entries = entries,
                isFirstPage = page == 1,
                isLastPage = entries.size < ACCOUNT_SUMMARY_PAGE_SIZE,
                isLoading = isLoading,
                onPrevPage = { page-- },
                onNextPage = { page++ },
                onSelectEntry = { viewModel.selectEntry(it) }
            )
        }
    }
}

class AccountSummaryViewModel(private val navController: NavController, val accountId: Long) : KoinComponent {
    private val accountRepository: AccountRepository by inject()
    private val entryRepository: EntryRepository by inject()

    val account by lazy {
        accountRepository.findById(accountId)
    }

    fun getEntries(page: Int = 1, perPage: Int = 4) =
        entryRepository.getAllForAccount(accountId, ((page - 1) * perPage).toLong(), perPage.toLong())

    fun navigateBack() {
        navController.navigateBack()
    }

    fun selectEntry(entry: EntryForAccount) {
        navController.navigate(Screen.EditTransaction.route(entry.transactionId))
    }
}