/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import org.koin.java.KoinJavaComponent.inject

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun CreateTransactionScreen(navController: NavController) {
    val db by remember { inject<Database>(Database::class.java) }
    val accountRepository by remember { inject<AccountRepository>(AccountRepository::class.java) }
    val categoryRepository by remember { inject<CategoryRepository>(CategoryRepository::class.java) }
    val transactionRepository by remember { inject<TransactionRepository>(TransactionRepository::class.java) }
    val entriesRepository by remember { inject<EntryRepository>(EntryRepository::class.java) }
    val snackbar by remember { inject<SnackbarHostState>(SnackbarHostState::class.java) }

    val accounts by accountRepository.allAsFlow().mapToList().collectAsState(listOf())
    val categories by categoryRepository.allAsFlow().mapToList().map { list ->
        list.map { it.toDto() }
    }.collectAsState(listOf())

    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<CategoryWithParent?>(null) }
    val entries = remember { mutableStateListOf<NewEntryDto>() }
    var newEntryDto by remember { mutableStateOf(makeEmptyNewEntryDto()) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    fun createTransaction(description: String, entries: List<NewEntryDto>) {
        if (description.trim().isEmpty()) {
            GlobalScope.launch {
                snackbar.showSnackbar("You need a description")
            }

            return
        }

        db.transaction {
            val number = transactionRepository.create(category!!.categoryId, description.trim())
            val transactionId = transactionRepository.findByReference(number)!!.transactionId

            entries.forEach {
                entriesRepository.create(
                    transactionId,
                    it.account!!.accountId,
                    it.amount,
                    it.incurredAt.atTime(0, 0),
                    it.recordedAt.atTime(0, 0)
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
        } else {
            createTransaction(description, entries)
        }
    }


    fun handleAddEntry(entry: NewEntryDto) {
        entries.add(entry)
        newEntryDto = makeEmptyNewEntryDto()
    }

    fun handleReset() {
        entries.removeAll { true }
        description = ""
    }

    val scroll = rememberScrollState()
    Column(
        modifier = Modifier.scrollable(scroll, orientation = Orientation.Vertical),
        verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        Text("Create a transaction", style = MaterialTheme.typography.h5)
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("To what end the money was moved? (Beer night, Salary, Bonus)") })

        Spacer(modifier = Modifier.fillMaxWidth())

        CategoryDropdown(
            expanded = isCategoryDropdownExpanded,
            onExpandedChange = { isCategoryDropdownExpanded = it },
            value = category,
            onSelect = { category = it },
            categories = categories
        ) {
            Row {
                OutlinedTextField(value = category?.name ?: "none",
                    onValueChange = {},
                    label = {
                        Text("Subcategory of", modifier = Modifier.clickable(true) {
                            isCategoryDropdownExpanded = !isCategoryDropdownExpanded
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "dropdown icon",
                            modifier = Modifier.clickable(true) {
                                isCategoryDropdownExpanded = !isCategoryDropdownExpanded
                            }
                        )
                    })
            }
        }

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

        Spacer(modifier = Modifier.fillMaxWidth())

        AddEntryForm(
            value = newEntryDto,
            onValueChanged = { newEntryDto = it },
            accounts = accounts
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                Button(onClick = { handleAddEntry(newEntryDto) }) { Text("Add") }

                TextButton(onClick = { newEntryDto = makeEmptyNewEntryDto() }) { Text("Reset") }
            }
        }
    }
}