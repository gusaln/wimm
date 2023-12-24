/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.navigation

import app.cash.sqldelight.coroutines.mapToList
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.data.EntryForTransaction
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.EntryRepository
import me.gustavolopezxyz.common.db.TransactionRepository
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class OverviewComponent(
    componentContext: ComponentContext,
    override val di: DI,
    val onEditTransaction: (transactionId: Long) -> Unit,
    val onDuplicateTransaction: (transaction: MoneyTransaction, entries: List<EntryForTransaction>) -> Unit
) : DIAware, ComponentContext by componentContext {
    val transactionRepository: TransactionRepository by instance()
    val categoryRepository: CategoryRepository by instance()
    val accountRepository: AccountRepository by instance()
    private val entryRepository: EntryRepository by instance()

    var summary: MutableValue<SummaryType> = MutableValue(SummaryType.Balance)

    fun onShowSummary(summaryType: SummaryType) {
        summary.value = summaryType
    }

    fun getEntries(transactionIds: Collection<Long>) = entryRepository.getAllForTransactions(transactionIds)

    fun getTransactionsAsFlow(page: Int = 1, perPage: Int = 15) =
        transactionRepository.getAsFlow(((page - 1) * perPage), perPage)

    fun getCategoriesByIdAsFlow() = categoryRepository.allAsFlow().mapToList(Dispatchers.IO).map { list ->
        list.map { it.toDto() }.associateBy { it.categoryId }
    }
}

enum class SummaryType {
    Balance, Owned, Debt, Expenses, Incomes;

    override fun toString(): String {
        return when (this) {
            Balance -> "Balance"

            Owned -> "Owned"

            Debt -> "Debt"

            Expenses -> "Expenses"

            Incomes -> "Incomes"
        }
    }

    companion object {
        val All: List<SummaryType> get() = listOf(Balance, Owned, Debt, Expenses, Incomes)
    }
}