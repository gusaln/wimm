/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.ext.toMoney
import org.koin.java.KoinJavaComponent

@Composable
fun DashboardScreen(navController: NavController) {
    val entriesRepository by remember { KoinJavaComponent.inject<EntryRepository>(EntryRepository::class.java) }

    var page by remember { mutableStateOf(1L) }
    var limit by remember { mutableStateOf(15L) }

    val entries = entriesRepository.asFlow((page - 1) * limit, limit).mapToList().map { entryList ->
        entryList.map { entry ->
            ListEntryDto(
                entry.entryId,
                entry.transactionDescription,
                entry.transactionId,
                entry.accountName,
                entry.amount.toMoney(entry.currency),
                entry.incurredAt,
                entry.recordedAt
            )
        }
    }.collectAsState(listOf(), Dispatchers.IO)

    RegularLayout(menu = { Text("Empty real state") }) {
        Column(verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { navController.navigate(Screen.CreateTransaction.route) }) { Text("Create entry") }
            }

            EntriesList(
                entries = entries.value,
                onEdit = {
                    navController.navigate(
                        Screen.EditTransaction.route,
                        Screen.EditTransaction.withArguments(it.transactionId)
                    )
                }
            )
        }
    }
}