/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import app.cash.sqldelight.coroutines.mapToList
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atTime
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.datetime.atStartOfDay
import me.gustavolopezxyz.db.SelectEntriesForTransaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class EditTransactionComponent(
    componentContext: ComponentContext,
    override val di: DI,
    val transactionId: Long,
    val onNavigateBack: () -> Unit,
) : DIAware, ComponentContext by componentContext {
    private val db: Database by instance()
    private val accountRepository: AccountRepository by instance()
    private val transactionRepository: TransactionRepository by instance()
    private val categoryRepository: CategoryRepository by instance()
    private val entryRepository: EntryRepository by instance()


    fun getTransaction() = transactionRepository.findByIdOrNull(transactionId)

    fun getEntries() = entryRepository.getAllForTransaction(transactionId)

    @Composable
    fun collectAccountsAsState() =
        accountRepository.allAsFlow().mapToList(Dispatchers.IO).map { list -> list.sortedBy { it.name } }
            .collectAsState(emptyList())

    @Composable
    fun collectCategoriesAsState() = categoryRepository.allAsFlow().mapToList(Dispatchers.IO).map { list ->
        list.map { it.toDto() }.sortedBy { it.fullname() }
    }.collectAsState(emptyList())


    fun editTransaction(
        transactionId: Long,
        categoryId: Long,
        incurredAt: LocalDate,
        description: String,
        details: String? = null,
        currency: Currency,
        entryMap: Map<Long, SelectEntriesForTransaction>,
        toCreate: Collection<NewEntryDto>,
        toModify: Collection<ModifiedEntryDto>,
    ): Result {
        if (description.isBlank()) {
            return Result.Error("You need a description")
        }


        if ((toModify.count { !it.toDelete } + toCreate.size) < 1) {
            return Result.Error("You need at least one entry")
        }

        val modifiedEntryWithoutCurrency = toModify.firstOrNull { it.amount.currency == MissingCurrency }
        if (modifiedEntryWithoutCurrency != null) {
            return Result.ModifiedEntryError("All entries require a Currency", modifiedEntryWithoutCurrency.id)
        }

        val newEntryWithoutCurrency = toCreate.firstOrNull { it.amount.currency == MissingCurrency }
        if (newEntryWithoutCurrency != null) {
            return Result.NewEntryError("All entries require a Currency", newEntryWithoutCurrency.id)
        }

        db.transaction {
            val newTotal = toCreate.sumOf { it.amountValue } + toModify.filter { !it.toDelete }.sumOf { it.amountValue }
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
                entryRepository.create(
                    transactionId,
                    it.accountId!!,
                    it.amountValue,
                    it.recordedAt.atTime(0, 0),
                    it.reference
                )
            }

            toModify.filter { it.toDelete }.forEach {
                val entry = entryMap.getValue(it.id)
                entryRepository.delete(entry.entryId, entry.accountId, entry.amount)
            }

            toModify.filter { it.wasEdited }.forEach {
                val original = entryMap.getValue(it.id)

                entryRepository.edit(
                    original.toEntry(),
                    it.toEntry(original.transactionId),
                )
            }
        }

        return Result.Success
    }

    fun deleteTransaction(transactionId: Long): Result {
        transactionRepository.delete(transactionId)

        return Result.Success
    }

    fun onTransactionChanged() {
        onNavigateBack()
    }

    fun onTransactionDeleted() {
        onNavigateBack()
    }

    sealed class Result {
        data object Success : Result()

        data class Error(val message: String) : Result()

        data class NewEntryError(val message: String, val entryId: Long) : Result()

        data class ModifiedEntryError(val message: String, val entryId: Long) : Result()
    }
}