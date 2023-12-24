/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.MissingCurrency
import me.gustavolopezxyz.common.data.emptyNewEntryDto
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.datetime.atStartOfDay
import me.gustavolopezxyz.common.ext.datetime.nowLocalDateTime
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class CreateTransactionComponent(override val di: DI) : DIAware {
    private val db: Database by instance()
    private val accountRepository: AccountRepository by instance()
    private val categoryRepository: CategoryRepository by instance()
    private val transactionRepository: TransactionRepository by instance()
    private val entriesRepository: EntryRepository by instance()

    var description = mutableStateOf("")
    var details = mutableStateOf("")
    var categoryId = mutableStateOf<Long?>(null)
    var incurredAt = mutableStateOf(nowLocalDateTime().date)
    val entries = mutableStateListOf(emptyNewEntryDto(nowLocalDateTime().date))

    fun reset() {
        description.value = ""
        details.value = ""
        categoryId.value = null
        incurredAt.value = nowLocalDateTime().date
        entries.clear()
        entries += emptyNewEntryDto(nowLocalDateTime().date)
    }

    @Composable
    fun collectAccountsAsState() =
        accountRepository.allAsFlow().mapToList(Dispatchers.IO).map { list -> list.sortedBy { it.name } }
            .collectAsState(emptyList())

    @Composable
    fun collectCategoriesAsState() = categoryRepository.allAsFlow().mapToList(Dispatchers.IO).map { list ->
        list.map { it.toDto() }.sortedBy { it.fullname() }
    }.collectAsState(emptyList())

    fun createTransaction(): Result {
        if (categoryId.value == null) {
            return Result.Error("You need to select a category", "categoryId")
        }

        if (description.value.trim().isEmpty()) {
            return Result.Error("You need a description", "description")
        }

        if (entries.isEmpty()) {
            return Result.Error("You need to add at least one entry", "entries")
        }

        val entryWithoutAccount = entries.firstOrNull { it.accountId == null }
        if (entryWithoutAccount != null) {
            return Result.EntryError("All entries require an Account", entryWithoutAccount.id)
        }

        val entryWithoutCurrency = entries.firstOrNull { it.amount.currency == MissingCurrency }
        if (entryWithoutCurrency != null) {
            return Result.EntryError("All entries require a Currency", entryWithoutCurrency.id)
        }

        db.transaction {
            val number = transactionRepository.create(categoryId.value!!,
                incurredAt.value.atStartOfDay(),
                description.value,
                details.value,
                entries.first().amountCurrency,
                entries.sumOf { it.amountValue })
            val transactionId = transactionRepository.findByReference(number)!!.transactionId

            entries.forEach {
                entriesRepository.create(
                    transactionId, it.accountId!!, it.amountValue, it.recordedAt.atStartOfDay(), it.reference
                )
            }
        }

        return Result.Success
    }

    sealed class Result {
        data object Success : Result()

        data class Error(val message: String, val parameter: String? = null) : Result()

        data class EntryError(val message: String, val entryId: Long) : Result()
    }
}