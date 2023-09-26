/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.datetime.atStartOfDay
import me.gustavolopezxyz.common.ext.datetime.nowLocalDateTime
import me.gustavolopezxyz.common.ui.CategoryDropdown
import me.gustavolopezxyz.common.ui.NewEntriesList
import me.gustavolopezxyz.common.ui.TotalListItem
import me.gustavolopezxyz.common.ui.common.AppButton
import me.gustavolopezxyz.common.ui.common.AppTextButton
import me.gustavolopezxyz.common.ui.common.OutlinedDateTextField
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

class CreateTransactionViewModel : KoinComponent {
    private val db: Database by inject()
    private val accountRepository: AccountRepository by inject()
    private val categoryRepository: CategoryRepository by inject()
    private val transactionRepository: TransactionRepository by inject()
    private val entriesRepository: EntryRepository by inject()
    val snackbar: SnackbarHostState by inject()

    @Composable
    fun getAccounts() =
        accountRepository.allAsFlow().mapToList(Dispatchers.IO).map { list -> list.sortedBy { it.name } }
            .collectAsState(emptyList())

    @Composable
    fun getCategories() = categoryRepository.allAsFlow().mapToList(Dispatchers.IO).map { list ->
        list.map { it.toDto() }.sortedBy { it.fullname() }
    }.collectAsState(emptyList())

    suspend fun createTransaction(
        description: String,
        details: String,
        category: Category,
        incurredAt: LocalDate,
        currency: Currency,
        entries: List<NewEntryDto>
    ) {
        if (description.trim().isEmpty()) {
            snackbar.showSnackbar("You need a description")

            return
        }

        if (entries.isEmpty()) {
            snackbar.showSnackbar("You need to add at least one entry")

            return
        }

        db.transaction {
            val number = transactionRepository.create(
                category.categoryId,
                incurredAt.atStartOfDay(),
                description,
                details,
                currency,
                entries.asIterable().sumOf { it.amount }
            )
            val transactionId = transactionRepository.findByReference(number)!!.transactionId

            entries.forEach {
                entriesRepository.create(
                    transactionId,
                    it.account!!.accountId,
                    it.amount,
                    it.recordedAt.atStartOfDay(),
                    it.reference
                )
            }
        }
    }
}

@Composable
fun CreateTransactionScreen(onCreate: () -> Unit = {}, onCancel: (() -> Unit)? = null) {
    val viewModel by remember { inject<CreateTransactionViewModel>(CreateTransactionViewModel::class.java) }

    val scope = rememberCoroutineScope()

    val accounts by viewModel.getAccounts()
    val categories by viewModel.getCategories()

    var description by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<CategoryWithParent?>(null) }
    var incurredAt by remember { mutableStateOf(nowLocalDateTime().date) }
    val entries = remember { mutableStateListOf(emptyNewEntryDto(nowLocalDateTime().date)) }

    val onCreateHook by rememberUpdatedState(onCreate)

    fun handleIncurredAtUpdate(value: LocalDate) {
        val oldValue = incurredAt.toString()
        incurredAt = value

        scope.launch {
            entries.forEachIndexed { index, entry ->
                if (entry.recordedAt.toString() == oldValue) {
                    entries[index] = entry.copy(recordedAt = value)
                }
            }
        }
    }

    fun handleCreate() {
        if (category == null) {
            scope.launch {
                viewModel.snackbar.showSnackbar("You need to select a category")
            }
        } else {
            scope.launch {
                viewModel.createTransaction(
                    description,
                    details,
                    category!!.toCategory(),
                    incurredAt,
                    entries.firstOrNull()?.account?.getCurrency() ?: MissingCurrency,
                    entries
                )
                onCreateHook()
            }
        }
    }

    fun handleAddEntry() {
        entries.add(emptyNewEntryDto())
    }

    fun handleReset() {
        entries.removeAll { true }
        description = ""
    }

    val scroll = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scroll).padding(AppDimensions.Default.padding.large),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
    ) {
        Text("Create a transaction", style = MaterialTheme.typography.h5)

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { description = if (it.isNotEmpty()) it.trimStart() else it },
            label = { Text("Description") },
            placeholder = { Text("To what end the money was moved? (Beer night, Salary, Bonus)") })

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
            onSelect = { category = it },
            categories = categories
        )

        Spacer(modifier = Modifier.fillMaxWidth())

        NewEntriesList(
            accounts = accounts,
            entries = entries,
            onEdit = { entry ->
                entries[entries.indexOfFirst { entry.id == it.id }] = entry
            },
            onDelete = { entry -> entries.removeIf { entry.id == it.id } },
            name = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Entries", style = MaterialTheme.typography.h5)

                    IconButton(onClick = ::handleAddEntry) {
                        Icon(Icons.Default.Add, "add new entry")
                    }
                }
            }
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
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small, Alignment.End)
        ) {
            AppButton(onClick = ::handleCreate, "Create")

            AppTextButton(onClick = ::handleReset, "Reset")
            if (onCancel != null) {
                AppTextButton(onClick = onCancel, "Cancel")
            }
        }
    }
}