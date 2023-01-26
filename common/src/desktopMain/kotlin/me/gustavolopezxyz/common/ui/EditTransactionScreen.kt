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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.*
import kotlinx.datetime.atTime
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.RecordRepository
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.db.SelectEntriesFromRecord
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent.inject

class EditTransactionViewModel : KoinComponent {
    private val db by inject<Database>(Database::class.java)
    private val accountRepository by inject<AccountRepository>(AccountRepository::class.java)
    private val recordRepository by inject<RecordRepository>(RecordRepository::class.java)
    private val entriesRepository by inject<EntryRepository>(EntryRepository::class.java)

    private val snackbar by inject<SnackbarHostState>(SnackbarHostState::class.java)

    fun getRecord(recordId: Long) = recordRepository.findById(recordId)

    fun getAccounts() = accountRepository.getAll()

    fun getEntries(recordId: Long) = entriesRepository.getByRecordId(recordId)

    suspend fun editRecord(
        recordId: Long,
        description: String,
        entryMap: Map<Long, SelectEntriesFromRecord>,
        toCreate: Collection<NewEntryDto>,
        toModify: Collection<EditEntryDto>,
    ) {
        if (description.trim().isEmpty()) {
            snackbar.showSnackbar("You need a description")

            return
        }

        if ((toModify.count { !it.to_delete } + toCreate.size) < 1) {
            snackbar.showSnackbar("You need at least one entry")

            return
        }

        db.transaction {
            recordRepository.update(recordId, description.trim())

            toCreate.forEach {
                entriesRepository.create(
                    it.description,
                    it.amount.toMoney(it.account!!.getCurrency()),
                    it.account.id,
                    recordId,
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
                    original.toEntry(),
                    it.toEntry(original.record_id),
                )
            }
        }

        snackbar.showSnackbar("Transaction modified")
    }

    fun deleteRecord(recordId: Long) {
        recordRepository.delete(recordId)
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun EditTransactionScreen(navController: NavController, transactionRecordId: Long) {
    val viewModel by remember { inject<EditTransactionViewModel>(EditTransactionViewModel::class.java) }

    val record by remember { mutableStateOf(viewModel.getRecord(transactionRecordId)) }
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

    val accounts = remember { viewModel.getAccounts() }
    val entryMap = remember {
        viewModel.getEntries(record!!.id).associateBy { it.id }
    }

    val toCreate = remember { mutableStateListOf<NewEntryDto>() }
    val toModify = remember {
        entryMap.map { entry ->
            makeEditEntryDtoFrom(entry.value)
        }.toMutableStateList()
    }

    var description by remember { mutableStateOf(record!!.description) }

    val scroll = rememberScrollState()
    var confirmDelete by remember { mutableStateOf(false) }

    fun editRecord() {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.editRecord(record!!.id, description, entryMap, toCreate, toModify)

//            withContext(Dispatchers.Main) {
            navController.navigateBack()
//            }
        }
    }

    fun deleteRecord() {
        confirmDelete = false

        GlobalScope.launch(Dispatchers.IO) {
            viewModel.deleteRecord(record!!.id)

//            withContext(Dispatchers.Main) {
            navController.navigateBack()
//            }
        }
    }


    if (confirmDelete) {
        Dialog(onCloseRequest = { confirmDelete = false }) {
            Card(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(Constants.Size.Medium.dp)) {
                        Text("Do you really want to delete this transaction?")
                    }

                    Spacer(modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        Button(onClick = ::deleteRecord) { Text("Delete") }
                        TextButton(onClick = { confirmDelete = false }) { Text("Cancel") }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scroll).padding(Constants.Size.Large.dp),
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Edit transaction", style = MaterialTheme.typography.h5)
            Button(onClick = { confirmDelete = !confirmDelete }) {
                Text("Delete")
            }
        }
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
            Button(onClick = ::editRecord) { Text("Edit") }
            TextButton(onClick = { navController.navigateBack() }) {
                Text("Go Back")
            }
        }
    }
}

@Composable
private fun TransactionCurrentEntriesSection(
    accounts: List<Account>,
    entries: List<SelectEntriesFromRecord>,
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
                        toModify.filter { !it.to_delete }.groupBy { it.account_currency.toCurrency() }
                            .mapValues { mapEntry ->
                                mapEntry.value.map { it.amount }.reduceOrNull { acc, amount -> acc + amount } ?: 0.0
                            }
                    }
                }

                val prevTotal by remember {
                    derivedStateOf {
                        entries.groupBy { entry -> entry.account_currency.toCurrency() }
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


