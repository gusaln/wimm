/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.EntryForTransaction
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.data.toEntryForTransaction
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ext.toSimpleFormat
import me.gustavolopezxyz.common.ui.core.MoneyText
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
    viewModel: TransactionsListViewModel,
    onSelect: (MoneyTransaction) -> Unit
) {
//    var page by remember { mutableStateOf(1L) }
//    var limit by remember { mutableStateOf(15L) }

    val transactions by viewModel.getTransactions().mapToList()
        .collectAsState(emptyList())
    val entriesByTransaction by viewModel.getEntries(transactions.map { it.transactionId }).mapToList()
        .map { list ->
            list.map { it.toEntryForTransaction() }.groupBy { it.transactionId }
        }
        .collectAsState(emptyMap())

    val scroll = rememberScrollState()

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.Large.dp, alignment = Alignment.Top)
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
    Card(modifier = Modifier.clickable { onSelect(transaction) }, elevation = 4.dp) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
        ) {
            Text(
                transaction.description,
                fontSize = 1.2.em,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis
            )

            entries.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    // Description and date
                    Column(verticalArrangement = Arrangement.spacedBy(Constants.Size.Small.dp)) {
                        Text(entry.accountName)

                        Text(
                            entry.incurredAt.toSimpleFormat(),
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