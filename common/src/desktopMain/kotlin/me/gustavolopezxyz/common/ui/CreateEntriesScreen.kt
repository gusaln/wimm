/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.RecordRepository
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.db.Database
import org.koin.java.KoinJavaComponent.inject

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun CreateEntriesScreen(navController: NavController) {
    val db by remember { inject<Database>(Database::class.java) }
    val accountRepository by remember { inject<AccountRepository>(AccountRepository::class.java) }
    val recordRepository by remember { inject<RecordRepository>(RecordRepository::class.java) }
    val entriesRepository by remember { inject<EntryRepository>(EntryRepository::class.java) }
    val snackbar by remember { inject<SnackbarHostState>(SnackbarHostState::class.java) }

    val accounts by accountRepository.allAsFlow().mapToList().collectAsState(listOf())


    var description by remember { mutableStateOf("") }
    val entries = remember { mutableStateListOf<NewEntryDto>() }

    var singleEntryMode by remember { mutableStateOf(true) }
    val snackbarHost by remember { inject<SnackbarHostState>(SnackbarHostState::class.java) }

    fun createTransaction(description: String, entries: List<NewEntryDto>) {
        db.transaction {
            val reference = recordRepository.create(description)
            val recordId = recordRepository.findByReference(reference)!!.id

            entries.forEach {
                entriesRepository.create(
                    it.description,
                    it.amount.toMoney(it.account!!.getCurrency()),
                    it.account.id,
                    recordId,
                    it.incurred_at.atTime(0, 0),
                    it.recorded_at.atTime(0, 0)
                )
            }

            GlobalScope.launch {
                snackbar.showSnackbar("Transaction recorded")
            }

            navController.navigate(Screen.Dashboard.name)
        }
    }

    fun handleCreate() {
        if (entries.size < 1) {
            GlobalScope.launch {
                snackbarHost.showSnackbar("You need to add at least one entry")
            }
        } else if (entries.size == 1) {
            createTransaction(entries[0].description, entries = entries)
        } else {
            createTransaction(description, entries)
        }
    }

    fun handleCreateWithSingle(entry: NewEntryDto) {
        entries.add(entry)
        handleCreate()
    }

    fun handleReset() {
        entries.removeAll { true }
        description = ""
    }

    fun handleChangeToSingleMode() {
        handleReset()
        singleEntryMode = true
    }

    val actionsColors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)

    if (singleEntryMode) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Constants.Size.Large.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                AddEntryForm(accounts, onAddEntry = ::handleCreateWithSingle, actionText = "Create") {
                    Button(
                        onClick = { singleEntryMode = false }, colors = actionsColors
                    ) { Text("Change to multi-part transaction") }
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Constants.Size.Large.dp),
            horizontalArrangement = Arrangement.spacedBy(Constants.Size.Large.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
            ) {
                AddEntryForm(accounts, onAddEntry = { entries.add(it) }) {
                    Button(onClick = ::handleChangeToSingleMode, colors = actionsColors) {
                        Text("Reset to single-entry transaction")
                    }
                }
            }

            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
            ) {
                Text("Create a Multi-part transaction", style = MaterialTheme.typography.h5)
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Awesome savings account") })

                Spacer(modifier = Modifier.fillMaxWidth())

                NewEntriesList(entries)

                Spacer(modifier = Modifier.fillMaxWidth())

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button(onClick = ::handleCreate) { Text("Create") }
                    Button(onClick = ::handleReset) { Text("Reset") }
                }
            }
        }
    }
}