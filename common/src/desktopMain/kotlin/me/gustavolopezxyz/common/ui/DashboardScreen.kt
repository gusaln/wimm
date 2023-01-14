/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.getAmount
import me.gustavolopezxyz.common.data.getIncurredAt
import me.gustavolopezxyz.common.data.getRecordedAt
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import org.koin.java.KoinJavaComponent

@Composable
fun DashboardScreen(navController: NavController) {
    val accountRepository: AccountRepository by KoinJavaComponent.inject(AccountRepository::class.java)
    val entriesRepository: EntryRepository by KoinJavaComponent.inject(EntryRepository::class.java)

    var page by remember { mutableStateOf(1L) }
    var limit by remember { mutableStateOf(15L) }


    val accounts by accountRepository.allAsFlow().mapToList().collectAsState(listOf())

    val entries by entriesRepository.asFlow((page - 1) * limit, limit)
        .mapToList()
        .map {
            val accounts = accountRepository
                .getById(it.map { entry -> entry.account_id }.toSet())
                .associateBy { account -> account.id }

            it.map { entry ->
                ListEntryDto(
                    entry.id,
                    entry.description,
                    accounts[entry.account_id]!!,
                    entry.getAmount(),
                    entry.getIncurredAt(),
                    entry.getRecordedAt()
                )
            }
        }
        .collectAsState(listOf())

    Row(
        modifier = Modifier.fillMaxWidth().padding(Constants.Size.LARGE.dp),
        horizontalArrangement = Arrangement.spacedBy(Constants.Size.LARGE.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Text("Emtpy real state")
        }

        Column(modifier = Modifier.weight(4f)) {
            RecordTransactionForm(accounts, {})

            EntriesList(entries, onPageUpdate = { page = it.toLong() }, onLimitUpdate = { limit = it.toLong() })
        }
    }
}