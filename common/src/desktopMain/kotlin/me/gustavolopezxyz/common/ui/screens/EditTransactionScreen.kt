/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.datetime.atStartOfDay
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.common.navigation.NavController
import me.gustavolopezxyz.common.ui.CategoryDropdown
import me.gustavolopezxyz.common.ui.ModifiedEntriesList
import me.gustavolopezxyz.common.ui.NewEntriesList
import me.gustavolopezxyz.common.ui.TotalListItem
import me.gustavolopezxyz.common.ui.common.AppButton
import me.gustavolopezxyz.common.ui.common.AppTextButton
import me.gustavolopezxyz.common.ui.common.OutlinedDateTextField
import me.gustavolopezxyz.common.ui.common.ScreenTitle
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.db.SelectEntriesForTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

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
                AppButton(onClick = { navController.navigateBack() }, "Back")
            }

            Card(modifier = Modifier.widthIn(200.dp, 400.dp).padding(AppDimensions.Default.padding.medium)) {
                Text("Transaction ID $transactionId not found")
            }
        }

        return
    }

    val accounts by viewModel.getAccounts()
    val categories by viewModel.getCategories()
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
    var details by remember { mutableStateOf(transaction!!.details ?: "") }
    var category by remember {
        mutableStateOf(categories.firstOrNull { it.categoryId == transaction!!.categoryId } ?: MissingCategory.toDto())
    }
    var incurredAt by remember { mutableStateOf(transaction!!.incurredAt.toLocalDateTime(currentTimeZone()).date) }
    LaunchedEffect(categories) {
        category = categories.firstOrNull { it.categoryId == transaction!!.categoryId } ?: MissingCategory.toDto()
    }

    fun handleIncurredAtUpdate(value: LocalDate) {
        val oldValue = incurredAt
        incurredAt = value

        scope.launch {
            toModify.forEachIndexed { index, entry ->
                if (entry.recordedAt == oldValue) {
                    toModify[index] = entry.copy(recordedAt = value)
                }
            }
        }
    }

    val scroll = rememberScrollState()
    var isConfirmingDelete by remember { mutableStateOf(false) }

    fun editRecord() {
        scope.launch(Dispatchers.IO) {
            viewModel.editTransaction(
                transaction!!.transactionId,
                category.categoryId,
                incurredAt,
                description,
                details,
                transaction!!.currency.toCurrency(),
                entryMap,
                toCreate,
                toModify
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
                        AppButton(onClick = ::deleteTransaction, "Delete")

                        AppTextButton(onClick = { isConfirmingDelete = false }, "Cancel")
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
            ScreenTitle {
                SelectionContainer {
                    Text("Edit transaction ${transaction?.number?.toString(16)}")
                }
            }

            AppButton(onClick = { isConfirmingDelete = !isConfirmingDelete }, "Delete")
        }
        Spacer(modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { description = it.trimStart() },
            label = { Text("Description") },
            placeholder = { Text("Awesome savings account") },
            singleLine = true,
            maxLines = 1
        )
        Spacer(modifier = Modifier.fillMaxWidth())

        OutlinedDateTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Date of the transaction") },
            date = incurredAt,
            onValueChange = { handleIncurredAtUpdate(it) }
        )

        Spacer(modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = details,
            onValueChange = { details = it.trimStart() },
            label = { Text("Details (optional)") },
            placeholder = { Text("10 in beer, 10 taxi, etc..") },
            singleLine = false,
            maxLines = 4
        )
        Spacer(modifier = Modifier.fillMaxWidth())

        CategoryDropdown(
            label = "Category",
            value = category,
            onSelect = { selected -> category = categories.first { it.categoryId == selected.categoryId } },
            categories = categories
        )
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
            AppButton(onClick = ::editRecord, "Edit")
            AppTextButton(onClick = { navController.navigateBack() }, "Go Back")
        }
    }
}

class EditTransactionViewModel(private val transactionId: Long) : KoinComponent {
    private val db by inject<Database>(Database::class.java)
    private val accountRepository by inject<AccountRepository>(AccountRepository::class.java)
    private val categoryRepository by inject<CategoryRepository>(CategoryRepository::class.java)
    private val transactionRepository by inject<TransactionRepository>(TransactionRepository::class.java)
    private val entriesRepository by inject<EntryRepository>(EntryRepository::class.java)
    val snackbar by inject<SnackbarHostState>(SnackbarHostState::class.java)

    fun getTransaction() = transactionRepository.findById(transactionId)

    @Composable
    fun getAccounts() =
        accountRepository.allAsFlow().mapToList(Dispatchers.IO).map { list -> list.sortedBy { it.name } }
            .collectAsState(emptyList())

    @Composable
    fun getCategories() = categoryRepository.allAsFlow().mapToList(Dispatchers.IO).map { list ->
        list.map { it.toDto() }.sortedBy { it.fullname() }
    }.collectAsState(emptyList())

    fun getEntries() = entriesRepository.getAllForTransaction(transactionId)

    suspend fun editTransaction(
        transactionId: Long,
        categoryId: Long,
        incurredAt: LocalDate,
        description: String,
        details: String? = null,
        currency: Currency,
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
            val newTotal = toCreate.asIterable().sumOf { it.amount } + toModify.filter { !it.toDelete }.asIterable()
                .sumOf { it.amount }
            transactionRepository.update(
                transactionId,
                categoryId,
                incurredAt.atStartOfDay(),
                description,
                details,
                currency,
                newTotal
            )

            toCreate.forEach {
                entriesRepository.create(
                    transactionId,
                    it.account!!.accountId,
                    it.amount,
                    it.recordedAt.atTime(0, 0),
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