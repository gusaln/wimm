/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.getAmount
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import org.koin.java.KoinJavaComponent

@Composable
fun DashboardScreen(navController: NavController) {
    val accountRepository by remember { KoinJavaComponent.inject<AccountRepository>(AccountRepository::class.java) }
    val entriesRepository by remember { KoinJavaComponent.inject<EntryRepository>(EntryRepository::class.java) }

    var page by remember { mutableStateOf(1L) }
    var limit by remember { mutableStateOf(15L) }

    val accounts = accountRepository.getAll().associateBy { account -> account.id }
    val entries = entriesRepository.asFlow((page - 1) * limit, limit).mapToList().map { entryList ->
        entryList.filter { accounts.contains(it.account_id) }.map { entry ->
            ListEntryDto(
                entry.id,
                entry.description,
                accounts[entry.account_id]!!,
                entry.getAmount(),
                entry.incurred_at,
                entry.recorded_at
            )
        }
    }.collectAsState(listOf(), Dispatchers.IO)

    RegularLayout(menu = { Text("Empty real state") }) {
        Column(verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)) {
            Row {
                Button(onClick = { navController.navigate(Screen.CreateEntries.route) }) { Text("Create entry") }
            }

            EntriesList(entries.value)
        }
    }
}