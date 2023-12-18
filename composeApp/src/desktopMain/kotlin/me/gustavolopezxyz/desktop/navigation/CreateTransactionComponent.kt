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
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.data.*
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
    constructor(
        di: DI,
        description: String? = null,
        details: String? = null,
        category: CategoryWithParent? = null,
        incurredAt: LocalDate? = null,
        entries: Collection<NewEntryDto>? = null,
    ) : this(di) {
        this.description.value = description ?: ""
        this.details.value = details ?: ""
        this.category.value = category
        this.incurredAt.value = incurredAt ?: nowLocalDateTime().date
        this.entries.clear()
        this.entries += entries ?: listOf(emptyNewEntryDto(nowLocalDateTime().date))
    }


    private val db: Database by instance()
    private val accountRepository: AccountRepository by instance()
    private val categoryRepository: CategoryRepository by instance()
    private val transactionRepository: TransactionRepository by instance()
    private val entriesRepository: EntryRepository by instance()

    var description = mutableStateOf("")
    var details = mutableStateOf("")
    var category = mutableStateOf<CategoryWithParent?>(null)
    var incurredAt = mutableStateOf(nowLocalDateTime().date)
    val entries = mutableStateListOf(emptyNewEntryDto(nowLocalDateTime().date))

    fun reset() {
        description.value = ""
        details.value = ""
        category.value = null
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
        if (category.value == null) {
            return Result.Error("You need to select a category")
        }

        if (description.value.trim().isEmpty()) {
            return Result.Error("You need a description")
        }

        if (entries.isEmpty()) {
            return Result.Error("You need to add at least one entry")
        }

        db.transaction {
            val number = transactionRepository.create(
                category.value!!.categoryId,
                incurredAt.value.atStartOfDay(),
                description.value,
                details.value,
                entries.firstOrNull()?.account?.getCurrency() ?: MissingCurrency,
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

        return Result.Success
    }

    sealed class Result {
        data object Success : Result()

        data class Error(val message: String) : Result()
    }
}