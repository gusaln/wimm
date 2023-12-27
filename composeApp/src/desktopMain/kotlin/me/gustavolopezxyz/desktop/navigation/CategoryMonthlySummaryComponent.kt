/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.sqldelight.coroutines.mapToList
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDateTime
import me.gustavolopezxyz.common.data.MoneyTransaction
import me.gustavolopezxyz.common.data.toDto
import me.gustavolopezxyz.common.db.CategoryRepository
import me.gustavolopezxyz.common.db.SelectTransactionsInCategoryInRange
import me.gustavolopezxyz.common.db.TransactionRepository
import me.gustavolopezxyz.common.ext.datetime.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import kotlin.coroutines.CoroutineContext

class CategoryMonthlySummaryComponent(
    componentContext: ComponentContext,
    override val di: DI,
    val categoryId: Long,
    val onSelectTransaction: (transaction: MoneyTransaction) -> Unit,
    val onNavigateBack: () -> Unit,
) : DIAware, ComponentContext by componentContext {
    private val categoryRepository: CategoryRepository by instance()
    private val transactionRepository: TransactionRepository by instance()

    val category by lazy {
        val c = categoryRepository.findById(categoryId)

        if (c.parentCategoryId != null) {
            c.toDto(categoryRepository.findById(c.parentCategoryId).name)
        } else {
            c.toDto()
        }
    }

    private val startOfPeriod = MutableValue(nowLocalDateTime().startOfMonth())

    private val endOfPeriod: LocalDateTime
        get() = startOfPeriod.value.endOfMonth()

    val month: Value<LocalDateTime> get() = startOfPeriod

    fun nextMonth() {
        startOfPeriod.value = startOfPeriod.value.nextMonth()
    }

    fun prevMonth() {
        startOfPeriod.value = startOfPeriod.value.prevMonth()
    }

    @Composable
    fun getTransactions(scope: CoroutineContext): State<List<SelectTransactionsInCategoryInRange>> {
        val startOfPeriod by startOfPeriod.subscribeAsState()

        return transactionRepository.getAllForCategoryInPeriodAsFlow(
            categoryId,
            startOfPeriod,
            startOfPeriod.endOfMonth()
        )
            .mapToList(Dispatchers.IO)
            .collectAsState(emptyList(), scope)
    }
}