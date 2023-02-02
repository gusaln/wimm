/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.common.ui.core.ScreenTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.db.SelectEntriesForTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

class EditTransactionViewModel(val transactionId: Long) : KoinComponent {
    private val db by inject<Database>(Database::class.java)
    private val accountRepository by inject<AccountRepository>(AccountRepository::class.java)
    private val categoriesRepository by inject<CategoryRepository>(CategoryRepository::class.java)
    private val transactionRepository by inject<TransactionRepository>(TransactionRepository::class.java)
    private val entriesRepository by inject<EntryRepository>(EntryRepository::class.java)
    val snackbar by inject<SnackbarHostState>(SnackbarHostState::class.java)

    fun getTransaction() = transactionRepository.findById(transactionId)

    fun getAccounts() = accountRepository.getAll()

    fun getCategories() = categoriesRepository.getAll()

    fun getEntries() = entriesRepository.getAllForTransaction(transactionId)

    suspend fun editTransaction(
        transactionId: Long,
        categoryId: Long,
        description: String,
        entryMap: Map<Long, SelectEntriesForTransaction>,
        toCreate: Collection<NewEntryDto>,
        toModify: Collection<ModifiedEntryDto>,
    ) {
        if (description.trim().isEmpty()) {
            snackbar.showSnackbar("You need a description")

            return
        }

        if ((toModify.count { !it.toDelete } + toCreate.size) < 1) {
            snackbar.showSnackbar("You need at least one entry")

            return
        }

        db.transaction {
            transactionRepository.update(transactionId, categoryId, description.trim())

            toCreate.forEach {
                entriesRepository.create(
                    transactionId,
                    it.account!!.accountId,
                    it.amount,
                    it.incurredAt.atTime(0, 0),
                    it.recordedAt.atTime(0, 0)
                )
            }

            toModify.filter { it.toDelete }.forEach {
                val entry = entryMap.getValue(it.id)
                entriesRepository.delete(entry.entryId, entry.accountId, entry.amount)
            }

            toModify.filter { it.wasEdited }.forEach {
                val original = entryMap.getValue(it.id)

                entriesRepository.edit(
                    original.toEntry(),
                    it.toEntry(original.transactionId),
                )
            }
        }
    }

    fun deleteTransaction(transactionId: Long) {
        transactionRepository.delete(transactionId)
    }
}

@Composable
fun EditTransactionScreen(navController: NavController, transactionId: Long) {
    val viewModel by remember {
        inject<EditTransactionViewModel>(EditTransactionViewModel::class.java) {
            parametersOf(transactionId)
        }
    }

    val scope = rememberCoroutineScope()

    val transaction by remember { mutableStateOf(viewModel.getTransaction()) }
    if (transaction == null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Button(onClick = { navController.navigateBack() }) {
                    Text("Back")
                }
            }

            Card(modifier = Modifier.widthIn(200.dp, 400.dp).padding(AppDimensions.Default.padding.medium)) {
                Text("Transaction ID $transactionId not found")
            }
        }

        return
    }

    val accounts = remember { viewModel.getAccounts() }
    val categories = remember { viewModel.getCategories().map { it.toDto() } }
    val entryMap = remember {
        viewModel.getEntries().associateBy { it.entryId }
    }

    val toCreate = remember { mutableStateListOf<NewEntryDto>() }
    val toModify = remember {
        entryMap.map { entry ->
            modifiedEntryDto(entry.value)
        }.toMutableStateList()
    }

    var description by remember { mutableStateOf(transaction!!.description) }
    var category by remember {
        mutableStateOf(categories.first { it.categoryId == transaction!!.categoryId })
    }

    val scroll = rememberScrollState()
    var isConfirmingDelete by remember { mutableStateOf(false) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    fun editRecord() {
        scope.launch(Dispatchers.IO) {
            viewModel.editTransaction(
                transaction!!.transactionId, category.categoryId, description, entryMap, toCreate, toModify
            )

            launch { viewModel.snackbar.showSnackbar("Transaction modified") }

            navController.navigateBack()
        }
    }

    fun deleteTransaction() {
        isConfirmingDelete = false

        scope.launch(Dispatchers.IO) {
            viewModel.deleteTransaction(transaction!!.transactionId)

            launch { viewModel.snackbar.showSnackbar("Transaction deleted") }

            navController.navigateBack()
        }
    }


    if (isConfirmingDelete) {
        Dialog(onCloseRequest = { isConfirmingDelete = false }) {
            Card(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(AppDimensions.Default.padding.medium)) {
                        Text("Do you really want to delete this transaction?")
                    }

                    Spacer(modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        Button(onClick = ::deleteTransaction) { Text("Delete") }
                        TextButton(onClick = { isConfirmingDelete = false }) { Text("Cancel") }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scroll).padding(AppDimensions.Default.padding.large),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScreenTitle("Edit transaction")

            Button(onClick = { isConfirmingDelete = !isConfirmingDelete }) {
                Text("Delete")
            }
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            placeholder = { Text("Awesome savings account") })

        Spacer(modifier = Modifier.fillMaxWidth())

        CategoryDropdown(
            expanded = isCategoryDropdownExpanded,
            onExpandedChange = { isCategoryDropdownExpanded = it },
            value = category,
            onSelect = { selected -> category = categories.first { it.categoryId == selected.categoryId } },
            categories = categories
        ) {
            Row {
                OutlinedTextField(value = category.fullname(), onValueChange = {}, label = {
                    Text("Category", modifier = Modifier.clickable(true) {
                        isCategoryDropdownExpanded = !isCategoryDropdownExpanded
                    })
                }, modifier = Modifier.fillMaxWidth(), readOnly = true, trailingIcon = {
                    Icon(imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "dropdown icon",
                        modifier = Modifier.clickable(true) {
                            isCategoryDropdownExpanded = !isCategoryDropdownExpanded
                        })
                })
            }
        }

        Spacer(modifier = Modifier.fillMaxWidth())

        ModifiedEntriesList(
            accounts = accounts,
            entries = toModify,
            onEdit = { entry ->
                toModify[toModify.indexOfFirst { entry.id == it.id }] = entry
            },
            onDelete = { entry -> toModify[toModify.indexOfFirst { entry.id == it.id }] = entry.delete() },
            onRestore = { entry -> toModify[toModify.indexOfFirst { entry.id == it.id }] = entry.restore() },
            name = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Modified entries", style = MaterialTheme.typography.h5)
                }
            }
        )

        NewEntriesList(
            accounts = accounts,
            entries = toCreate,
            onEdit = { entry ->
                toCreate[toCreate.indexOfFirst { entry.id == it.id }] = entry
            },
            onDelete = { entry -> toCreate.removeIf { entry.id == it.id } },
            name = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("New entries", style = MaterialTheme.typography.h5)

                    IconButton(onClick = { toCreate.add(emptyNewEntryDto()) }) {
                        Icon(Icons.Default.Add, "add new entry")
                    }
                }
            }
        )

        val prevTotal by remember {
            derivedStateOf {
                entryMap.values.toList().groupBy { entry -> entry.currency.toCurrency() }.mapValues { mapEntry ->
                    mapEntry.value.map { it.amount }.reduceOrNull { acc, amount -> acc + amount } ?: 0.0
                }
            }
        }

        val newTotal by remember {
            derivedStateOf {
                val amountsByCurrency = mutableMapOf<Currency, MutableList<Money>>()

                toModify.filter { !it.toDelete }.map { it.amount.toMoney(it.currency) }
                    .groupByTo(amountsByCurrency) { it.currency }
                toCreate.map { it.amount.toMoney((it.account ?: MissingAccount).currency) }
                    .groupByTo(amountsByCurrency) { it.currency }

                amountsByCurrency.mapValues { mapEntry ->
                    mapEntry.value.map { it.value }.reduceOrNull { acc, amount -> acc + amount } ?: 0.0
                }
            }
        }

        TotalListItem("Prev. Total", totalsByCurrency = prevTotal)
        TotalListItem("Modified Total", totalsByCurrency = newTotal)

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


