/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.data.toEntryForAccount
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.ext.toSimpleFormat
import me.gustavolopezxyz.common.ui.core.MoneyText
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountSummaryViewModel(private val accountId: Long) : KoinComponent {
    private val accountRepository: AccountRepository by inject()
    private val entryRepository: EntryRepository by inject()
    val account by lazy {
        accountRepository.findById(accountId)
    }

    fun getEntries(page: Int = 1, perPage: Int = 15) =
        entryRepository.getAllForAccount(accountId, ((page - 1) * perPage).toLong(), perPage.toLong())
}


@Composable
fun AccountSummary(viewModel: AccountSummaryViewModel) {
    var page by remember { mutableStateOf(1) }
    var perPage by remember { mutableStateOf(15) }

    val account = viewModel.account
    val entries by viewModel.getEntries(page, perPage).mapToList().map { list -> list.map { it.toEntryForAccount() } }
        .collectAsState(emptyList())

    Card(modifier = Modifier, elevation = 4.dp) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
        ) {
            // Header with account info
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(account.name, style = MaterialTheme.typography.h5)

                MoneyText(
                    account.balance,
                    account.getCurrency(),
                    valueStyle = TextStyle.Default.copy(fontSize = 1.6.em),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
            ) {
                entries.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Description and date
                        Column(verticalArrangement = Arrangement.spacedBy(Constants.Size.Small.dp)) {
                            Text(it.transactionDescription)

                            Text(
                                it.incurredAt.toSimpleFormat(),
                                fontSize = MaterialTheme.typography.caption.fontSize
                            )
                        }

                        MoneyText(
                            it.amount, account.getCurrency()
                        )
                    }
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


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Constants.Size.Large.dp, Alignment.CenterHorizontally)
            ) {
                IconButton(onClick = {}, enabled = page > 1) {
                    Icon(Icons.Default.KeyboardArrowLeft, "prev page")
                }

                IconButton(onClick = {}, enabled = entries.size == perPage) {
                    Icon(Icons.Default.KeyboardArrowRight, "next page")
                }
            }
        }
    }
}