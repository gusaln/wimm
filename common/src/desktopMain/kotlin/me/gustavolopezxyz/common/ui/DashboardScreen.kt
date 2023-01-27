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
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.db.EntryRepository
import org.koin.java.KoinJavaComponent

@Composable
fun DashboardScreen(navController: NavController) {
    val entriesRepository by remember { KoinJavaComponent.inject<EntryRepository>(EntryRepository::class.java) }

    var page by remember { mutableStateOf(1L) }
    var limit by remember { mutableStateOf(15L) }

    val entries by entriesRepository.asFlow((page - 1) * limit, limit).mapToList().map { list ->
        list.map { it.toDto() }
    }.collectAsState(emptyList(), Dispatchers.IO)

    RegularLayout(menu = { Text("Empty real state") }) {
        Column(verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = { navController.navigate(Screen.CreateTransaction.route) }) { Text("Create entry") }
            }

            EntriesList(
                entries = entries
            ) {
                navController.navigate(
                    Screen.EditTransaction.route,
                    Screen.EditTransaction.withArguments(it.transactionId)
                )
            }
        }
    }
}