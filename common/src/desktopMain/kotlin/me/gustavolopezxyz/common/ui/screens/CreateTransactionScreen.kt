/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.screens

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
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ui.CategoryDropdown
import me.gustavolopezxyz.common.ui.NewEntriesList
import me.gustavolopezxyz.common.ui.TotalListItem
import me.gustavolopezxyz.common.ui.common.AppButton
import me.gustavolopezxyz.common.ui.common.AppTextButton
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
    fun getAccounts() = accountRepository.allAsFlow().mapToList().collectAsState(emptyList())

    @Composable
    fun getCategories() = categoryRepository.allAsFlow().mapToList().map { list ->
        list.map { it.toDto() }.sortedBy { it.fullname() }
    }.collectAsState(emptyList())

    suspend fun createTransaction(description: String, category: Category, entries: List<NewEntryDto>) {
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
                description.trim(),
                entries.asIterable().sumOf { it.amount })
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
    var category by remember { mutableStateOf<CategoryWithParent?>(null) }
    val entries = remember { mutableStateListOf<NewEntryDto>() }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    val onCreateHook by rememberUpdatedState(onCreate)

    fun handleCreate() {
        if (category == null) {
            scope.launch {
                viewModel.snackbar.showSnackbar("You need to select a category")
            }
        } else {
            scope.launch {
                viewModel.createTransaction(description, category!!.toCategory(), entries)
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
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = category?.fullname() ?: "-",
                    onValueChange = {},
                    label = {
                        Text("Category", modifier = Modifier.clickable(true) {
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