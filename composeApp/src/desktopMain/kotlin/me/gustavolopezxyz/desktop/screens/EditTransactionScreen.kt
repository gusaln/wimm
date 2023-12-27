/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.money.Currency
import me.gustavolopezxyz.common.money.Money
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.EditTransactionComponent
import me.gustavolopezxyz.desktop.services.SnackbarService
import me.gustavolopezxyz.desktop.ui.CategoryDropdown
import me.gustavolopezxyz.desktop.ui.ModifiedEntriesList
import me.gustavolopezxyz.desktop.ui.NewEntriesList
import me.gustavolopezxyz.desktop.ui.TotalListItem
import me.gustavolopezxyz.desktop.ui.common.AppButton
import me.gustavolopezxyz.desktop.ui.common.AppTextButton
import me.gustavolopezxyz.desktop.ui.common.OutlinedDateTextField
import me.gustavolopezxyz.desktop.ui.common.ScreenTitle
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun EditTransactionScreen(component: EditTransactionComponent) {
    val scope = rememberCoroutineScope()

    val transaction by remember { mutableStateOf(component.getTransaction()) }
    if (transaction == null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                AppButton(onClick = component.onNavigateBack, "Back")
            }

            Card(modifier = Modifier.widthIn(200.dp, 400.dp).padding(AppDimensions.Default.padding.medium)) {
                Text("Transaction ID ${component.transactionId} not found")
            }
        }

        return
    }

    val di = localDI()
    val snackbar by di.instance<SnackbarService>()

    val accounts by component.collectAccountsAsState()
    val categories by component.collectCategoriesAsState()
    val entryMap = remember {
        component.getEntries().associateBy { it.entryId }
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

        toModify.forEachIndexed { index, entry ->
            if (entry.recordedAt == oldValue) {
                toModify[index] = entry.copy(recordedAt = value)
            }
        }
    }

    val scroll = rememberScrollState()
    var isConfirmingDelete by remember { mutableStateOf(false) }

    fun editRecord() {
        val result = component.editTransaction(
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

        if (result is EditTransactionComponent.Result.Error) {
            scope.launch {
                snackbar.showSnackbar(result.message)
            }
        } else {
            component.onTransactionChanged()
        }
    }

    fun deleteTransaction() {
        isConfirmingDelete = false

        component.deleteTransaction(transaction!!.transactionId)

        scope.launch {
            launch { snackbar.showSnackbar("Transaction deleted") }
        }

        component.onTransactionDeleted()
    }


    if (isConfirmingDelete) {
        DialogWindow(onCloseRequest = { isConfirmingDelete = false }) {
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

        OutlinedDateTextField(modifier = Modifier.fillMaxWidth(),
            label = { Text("Date of the transaction") },
            date = incurredAt,
            onValueChange = { handleIncurredAtUpdate(it) })

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

        ModifiedEntriesList(accounts = accounts,
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
                    Text("Modified entries", style = MaterialTheme.typography.headlineSmall)
                }
            })

        NewEntriesList(accounts = accounts, entries = toCreate, entryError = null, onEdit = { entry ->
            toCreate[toCreate.indexOfFirst { entry.id == it.id }] = entry
        }, onDelete = { entry -> toCreate.removeIf { entry.id == it.id } }, name = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New entries", style = MaterialTheme.typography.headlineSmall)

                IconButton(onClick = { toCreate.add(emptyNewEntryDto()) }) {
                    Icon(Icons.Default.Add, "add new entry")
                }
            }
        })

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

                toModify.filter { !it.toDelete }.map { it.amount }
                    .groupByTo(amountsByCurrency) { it.currency }
                toCreate.map { it.amount }
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
            AppTextButton(onClick = component.onNavigateBack, "Go Back")
        }
    }
}
