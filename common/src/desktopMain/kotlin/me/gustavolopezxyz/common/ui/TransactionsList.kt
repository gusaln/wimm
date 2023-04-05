/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.data.EntryForTransaction
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.datetime.formatDateTime
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ui.common.AppDivider
import me.gustavolopezxyz.common.ui.common.CardTitle
import me.gustavolopezxyz.common.ui.common.MoneyText
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionsListViewModel() : KoinComponent {
    private val transactionRepository: TransactionRepository by inject()
    private val entryRepository: EntryRepository by inject()
    private val categoryRepository: CategoryRepository by inject()

    fun getTransactionsAsFlow(page: Int = 1, perPage: Int = 15) =
        transactionRepository.getAsFlow(((page - 1) * perPage), perPage)

    fun getEntriesAsFlow(transactionIds: Collection<Long>) =
        entryRepository.getAllForTransactionsAsFlow(transactionIds)

    fun getEntries(transactionIds: Collection<Long>) = entryRepository.getAllForTransactions(transactionIds)

    fun getCategoriesAsFlow() = categoryRepository.allAsFlow()

    fun getCategoriesMapAsFlow() = getCategoriesAsFlow().mapToList().map { list ->
        list.map { it.toDto() }.associateBy { it.categoryId }
    }
}

@Composable
fun TransactionsList(
    transactions: List<MoneyTransaction>,
    entriesByTransaction: Map<Long, List<EntryForTransaction>>,
    onSelect: (MoneyTransaction) -> Unit
) {
    val scroll = rememberScrollState()

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large, alignment = Alignment.Top)
        ) {
            transactions.forEach { transaction ->
                TransactionEntresList(
                    transaction,
                    entriesByTransaction.getOrDefault(transaction.transactionId, emptyList()),
                    onSelect
                )
            }

            Spacer(Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionEntresList(
    transaction: MoneyTransaction,
    entries: List<EntryForTransaction>,
    onSelect: (MoneyTransaction) -> Unit,
) {
    Card() {
        Column(
            modifier = Modifier.padding(AppDimensions.Default.cardPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
        ) {
            CardTitle(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)
            ) {
                Text(
                    transaction.description,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                )

                Chip(
                    onClick = {},
                    modifier = Modifier.wrapContentSize(),
                    colors = ChipDefaults.chipColors(Color.Magenta.copy(.5f)),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("category", style = MaterialTheme.typography.caption)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { onSelect(transaction) }) {
                    Icon(Icons.Default.ArrowForward, "edit transaction")
                }
            }

            AppDivider()

            entries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Description and date
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)) {
                        Text(entry.accountName)

                        Text(
                            entry.recordedAt.formatDateTime(),
                            color = Color.Gray,
                            fontSize = MaterialTheme.typography.caption.fontSize
                        )
                    }

                    // Amount
                    MoneyText(entry.amount, entry.currency.toCurrency())
                }
            }
        }
    }
}