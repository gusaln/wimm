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
import androidx.compose.ui.unit.dp
import me.gustavolopezxyz.common.data.EntryForTransaction
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ext.toSimpleFormat
import me.gustavolopezxyz.common.ui.core.CardTitle
import me.gustavolopezxyz.common.ui.core.MoneyText
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionsListViewModel() : KoinComponent {
    private val transactionRepository: TransactionRepository by inject()
    private val entryRepository: EntryRepository by inject()

    fun getTransactions(page: Long = 1, perPage: Long = 15) =
        transactionRepository.getAsFlow((page - 1) * perPage, perPage)

    fun getEntries(transactionIds: Collection<Long>) =
        entryRepository.getAllForTransactionsAsFlow(transactionIds)
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
                TransactionEntryList(
                    transaction,
                    entriesByTransaction.getOrDefault(transaction.transactionId, emptyList()),
                    onSelect
                )
            }

            Spacer(Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun TransactionEntryList(
    transaction: MoneyTransaction,
    entries: List<EntryForTransaction>,
    onSelect: (MoneyTransaction) -> Unit,
) {
    Card(elevation = 4.dp) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
        ) {
            CardTitle(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small)
            ) {
                Text(
                    transaction.description,
                    modifier = Modifier.weight(1f),
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(onClick = { onSelect(transaction) }) {
                    Icon(Icons.Default.ArrowForward, "edit transaction")
                }
            }

            Divider()

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
                            entry.incurredAt.toSimpleFormat(),
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