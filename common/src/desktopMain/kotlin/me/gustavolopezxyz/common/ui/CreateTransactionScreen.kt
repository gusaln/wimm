/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.MissingAccount
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.RecordRepository
import me.gustavolopezxyz.common.ext.toMoney
import org.koin.java.KoinJavaComponent.inject

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun CreateTransactionScreen(navController: NavController) {
    val db by remember { inject<Database>(Database::class.java) }
    val accountRepository by remember { inject<AccountRepository>(AccountRepository::class.java) }
    val recordRepository by remember { inject<RecordRepository>(RecordRepository::class.java) }
    val entriesRepository by remember { inject<EntryRepository>(EntryRepository::class.java) }
    val snackbar by remember { inject<SnackbarHostState>(SnackbarHostState::class.java) }

    val accounts by accountRepository.allAsFlow().mapToList().collectAsState(listOf())

    var description by remember { mutableStateOf("") }
    val entries = remember { mutableStateListOf<NewEntryDto>() }
    var newEntryDto by remember { mutableStateOf(makeEmptyNewEntryDto()) }

    var singleEntryMode by remember { mutableStateOf(true) }

    fun createTransaction(description: String, entries: List<NewEntryDto>) {
        if (description.trim().isEmpty()) {
            GlobalScope.launch {
                snackbar.showSnackbar("You need a description")
            }

            return
        }

        db.transaction {
            val reference = recordRepository.create(description.trim())
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

            navController.navigateBack()
        }
    }

    fun handleCreate() {
        if (entries.size < 1) {
            GlobalScope.launch {
                snackbar.showSnackbar("You need to add at least one entry")
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

    val scroll = rememberScrollState()
    val specialActionColors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)

    if (singleEntryMode) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(scroll).padding(Constants.Size.Large.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                AddEntryForm(
                    value = newEntryDto,
                    onValueChanged = { newEntryDto = it },
                    accounts = accounts,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        Button(
                            onClick = { singleEntryMode = false }, colors = specialActionColors
                        ) {
                            Text("Change to multi-part transaction")
                        }

                        Spacer(modifier = Modifier)

                        Button(onClick = { handleCreateWithSingle(newEntryDto) }) { Text("Create") }

                        TextButton(onClick = { newEntryDto = makeEmptyNewEntryDto() }) { Text("Reset") }
                    }
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
                AddEntryForm(
                    value = newEntryDto,
                    onValueChanged = { newEntryDto = it },
                    accounts = accounts
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        Button(
                            onClick = {
                                handleReset()
                                singleEntryMode = true
                            }, colors = specialActionColors
                        ) {
                            Text("Reset to single-entry transaction")
                        }

                        Spacer(modifier = Modifier)

                        Button(onClick = { handleCreateWithSingle(newEntryDto) }) { Text("Create") }

                        TextButton(onClick = { newEntryDto = makeEmptyNewEntryDto() }) { Text("Reset") }
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

                NewEntriesList(
                    entries = entries,
                    onEdit = { entry ->
                        entries.removeIf { entry.uid == it.uid }
                        newEntryDto = entry
                    },
                    onDelete = { entry -> entries.removeIf { entry.uid == it.uid } }
                ) {
                    val totalsByCurrency by remember {
                        derivedStateOf {
                            entries
                                .groupBy { (it.account ?: MissingAccount).getCurrency() }
                                .mapValues { mapEntry ->
                                    mapEntry.value.map { it.amount }.reduceOrNull { acc, amount -> acc + amount } ?: 0.0
                                }
                        }
                    }

                    TotalListItem(totalsByCurrency = totalsByCurrency)
                }

                Spacer(modifier = Modifier.fillMaxWidth())

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button(onClick = ::handleCreate) { Text("Create") }
                    TextButton(onClick = ::handleReset) { Text("Reset") }
                }
            }
        }
    }
}