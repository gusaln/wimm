/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.MissingAccount
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.RecordRepository
import me.gustavolopezxyz.common.ext.currentTz
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.db.Account
import me.gustavolopezxyz.db.Database
import me.gustavolopezxyz.db.Entry
import org.koin.java.KoinJavaComponent.inject

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun EditTransactionScreen(navController: NavController, transactionRecordId: Long) {
    val db by remember { inject<Database>(Database::class.java) }
    val accountRepository by remember { inject<AccountRepository>(AccountRepository::class.java) }
    val recordRepository by remember { inject<RecordRepository>(RecordRepository::class.java) }
    val entriesRepository by remember { inject<EntryRepository>(EntryRepository::class.java) }
    val snackbar by remember { inject<SnackbarHostState>(SnackbarHostState::class.java) }

    val record by remember { mutableStateOf(recordRepository.findById(transactionRecordId)) }
    if (record == null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.Large.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Button(onClick = { navController.navigateBack() }) {
                    Text("Back")
                }
            }

            Card(modifier = Modifier.widthIn(200.dp, 400.dp).padding(Constants.Size.Medium.dp)) {
                Text("Transaction ID $transactionRecordId not found")
            }
        }

        return
    }

    val accounts = remember { accountRepository.getAll() }
    val entryMap = remember {
        entriesRepository.getByRecordId(record!!.id).associateBy { it.id }
    }

    val toCreate = remember { mutableStateListOf<NewEntryDto>() }
    val toModify = remember {
        entryMap.map { entry ->
            makeEditEntryDtoFrom(entry.value, accounts.find { it.id == entry.value.account_id }!!)
        }.toMutableStateList()
    }

    var description by remember { mutableStateOf(record!!.description) }

    fun editTransaction() {
        if (description.trim().isEmpty()) {
            GlobalScope.launch {
                snackbar.showSnackbar("You need a description")
            }

            return
        }

        if (toModify.count { !it.to_delete } + toCreate.size < 1) {
            GlobalScope.launch {
                snackbar.showSnackbar("You need at least one entry")
            }

            return
        }

        db.transaction {
            recordRepository.update(record!!, description.trim())

            toCreate.forEach {
                entriesRepository.create(
                    it.description,
                    it.amount.toMoney(it.account!!.getCurrency()),
                    it.account.id,
                    record!!.id,
                    it.incurred_at.atTime(0, 0),
                    it.recorded_at.atTime(0, 0)
                )
            }

            toModify.filter { it.to_delete }.forEach {
                val entry = entryMap.getValue(it.id)
                entriesRepository.delete(entry.id, entry.account_id, entry.amount_value)
            }

            toModify.filter { it.edited }.forEach {
                val original = entryMap.getValue(it.id)
                entriesRepository.edit(
                    original, original.copy(
                        description = it.description,
                        account_id = it.account.id,
                        amount_currency = it.account.balance_currency,
                        amount_value = it.amount,
                        incurred_at = it.incurred_at.atTime(0, 0, 0).toInstant(currentTz()),
                        recorded_at = it.recorded_at.atTime(0, 0, 0).toInstant(currentTz()),
                    )
                )
            }

            GlobalScope.launch {
                snackbar.showSnackbar("Transaction modified")
            }

            navController.navigateBack()
        }
    }


    val scroll = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scroll).padding(Constants.Size.Large.dp),
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        Text("Edit transaction", style = MaterialTheme.typography.h5)
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("Awesome savings account") })

        Spacer(modifier = Modifier.fillMaxWidth())

        TransactionCurrentEntriesSection(accounts, entryMap.values.toList(), toModify)

        Spacer(modifier = Modifier.fillMaxWidth())

        // New entries
        TransactionNewEntriesSection(accounts, toCreate)

        Spacer(modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            Button(onClick = ::editTransaction) { Text("Edit") }
            TextButton(onClick = { navController.navigateBack() }) {
                Text("Go Back")
            }
        }
    }
}

@Composable
private fun TransactionCurrentEntriesSection(
    accounts: List<Account>,
    entries: List<Entry>,
    toModify: SnapshotStateList<EditEntryDto>,
) {
    var editEntryDto by remember { mutableStateOf<EditEntryDto?>(null) }

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        // Current, modified, and deleted entries
        Column(modifier = Modifier.weight(1f)) {
            EditedEntriesList(entries = toModify, onEdit = { editEntryDto = it }, onDeleteToggle = { entry ->
                toModify[toModify.indexOf(entry)] = if (entry.to_delete) {
                    entry.restore()
                } else {
                    entry.delete()
                }
            }, totals = {
                val newTotal by remember {
                    derivedStateOf {
                        toModify.filter { !it.to_delete }.groupBy { it.account.getCurrency() }.mapValues { mapEntry ->
                            mapEntry.value.map { it.amount }.reduceOrNull { acc, amount -> acc + amount } ?: 0.0
                        }
                    }
                }

                val prevTotal by remember {
                    derivedStateOf {
                        entries.groupBy { entry -> (accounts.find { it.id == entry.account_id }!!).getCurrency() }
                            .mapValues { mapEntry ->
                                mapEntry.value.map { it.amount_value }.reduceOrNull { acc, amount -> acc + amount }
                                    ?: 0.0
                            }
                    }
                }

                TotalListItem("Prev. Total", totalsByCurrency = prevTotal)
                TotalListItem("Modified Total", totalsByCurrency = newTotal)
            })
        }

        // Edit entries form
        if (editEntryDto != null) {
            Column(modifier = Modifier.weight(1f)) {
                EditEntryForm(
                    value = editEntryDto!!,
                    onValueChanged = { editEntryDto = it },
                    accounts = accounts,
                    onEditEntry = {
                        toModify[toModify.indexOfFirst { e -> e.id == editEntryDto!!.id }] = editEntryDto!!
                        editEntryDto = null
                    },
                    onCancel = { editEntryDto = null },
                )
            }
        } else {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select an entry to modify it", color = Color.LightGray)
            }
        }
    }
}

@Composable
private fun TransactionNewEntriesSection(
    accounts: List<Account>, toCreate: SnapshotStateList<NewEntryDto>
) {
    var newEntryDto by remember { mutableStateOf<NewEntryDto?>(null) }

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            NewEntriesList(entries = toCreate, onEdit = { entry ->
                toCreate.removeIf { entry.uid == it.uid }
                newEntryDto = entry
            }, onDelete = { entry -> toCreate.removeIf { entry.uid == it.uid } }, name = {
                Text("New entries", style = MaterialTheme.typography.h5)
            }) {
                val totalsByCurrency by remember {
                    derivedStateOf {
                        toCreate.groupBy { (it.account ?: MissingAccount).getCurrency() }.mapValues { mapEntry ->
                            mapEntry.value.map { it.amount }.reduceOrNull { acc, amount -> acc + amount } ?: 0.0
                        }
                    }
                }

                TotalListItem("New entries Total", totalsByCurrency = totalsByCurrency)
            }
        }

        // New entries form
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            if (newEntryDto != null) {
                AddEntryForm(
                    value = newEntryDto!!, onValueChanged = { newEntryDto = it }, accounts = accounts
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        Button(onClick = {
                            toCreate.add(newEntryDto!!)
                            newEntryDto = null
                        }) { Text("Add") }

                        TextButton(onClick = { newEntryDto = null }) { Text("Cancel") }
                    }
                }
            } else {
                Button(
                    onClick = { newEntryDto = makeEmptyNewEntryDto() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                ) {
                    Text("Add a new entry")
                }
            }
        }
    }
}


