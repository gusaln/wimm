/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import me.gustavolopezxyz.common.data.MissingAccount
import me.gustavolopezxyz.common.data.emptyNewEntryDto
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.desktop.navigation.CreateTransactionComponent
import me.gustavolopezxyz.desktop.services.SnackbarService
import me.gustavolopezxyz.desktop.ui.CategoryDropdown
import me.gustavolopezxyz.desktop.ui.NewEntriesList
import me.gustavolopezxyz.desktop.ui.TotalListItem
import me.gustavolopezxyz.desktop.ui.common.AppButton
import me.gustavolopezxyz.desktop.ui.common.AppTextButton
import me.gustavolopezxyz.desktop.ui.common.OutlinedDateTextField
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun CreateTransactionScreen(
    component: CreateTransactionComponent,
    onCreate: () -> Unit = {},
    onCancel: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    val accounts by component.collectAccountsAsState()
    val categories by component.collectCategoriesAsState()

    var description by remember { component.description }
    var details by remember { component.details }
    var category by remember { component.category }
    var incurredAt by remember { component.incurredAt }
    val entries = remember { component.entries }

    val di = localDI()
    val onCreateHook by rememberUpdatedState(onCreate)

    val snackbar by di.instance<SnackbarService>()
    var oldValue by remember { mutableStateOf(incurredAt.toString()) }
    LaunchedEffect(incurredAt) {
        entries.forEachIndexed { index, entry ->
            if (entry.recordedAt.toString() == oldValue) {
                entries[index] = entry.copy(recordedAt = incurredAt)
            }
        }

        oldValue = incurredAt.toString()
    }

    fun handleCreate() {
        val result = component.createTransaction()

        if (result is CreateTransactionComponent.Result.Error) {
            scope.launch {
                snackbar.showSnackbar(result.message)
            }
        } else {
            onCreateHook()
        }
    }

    fun handleAddEntry() {
        entries.add(emptyNewEntryDto())
    }

    fun handleReset() {
        component.reset()
    }

    val scroll = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scroll).padding(AppDimensions.Default.padding.large),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)
    ) {
        Text("Create a transaction", style = MaterialTheme.typography.headlineSmall)

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
            onValueChange = { incurredAt = it }
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
                    Text("Entries", style = MaterialTheme.typography.headlineSmall)

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