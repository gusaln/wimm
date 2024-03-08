/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDateTime
import me.gustavolopezxyz.common.data.ExchangeRate
import me.gustavolopezxyz.common.db.ExchangeRateRepository
import me.gustavolopezxyz.common.money.Currency
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class ManageExchangeRatesComponent(
    componentContext: ComponentContext,
    override val di: DI,
) : DIAware, ComponentContext by componentContext {
    private val exchangeRateRepository: ExchangeRateRepository by instance()

    val page = MutableValue(1)
    val pageSize = MutableValue(15)

    fun onNextPage() {
        page.value++
    }

    fun onPrevPage() {
        page.value--
    }

    fun insertMany(list: List<UnsavedExchangeRate>) {
        list.forEach {
            exchangeRateRepository.create(it.baseCurrency, it.counterCurrency, it.rate, it.effectiveSince.date)
        }
    }

    @Composable
    fun collectExchangeRatesAsState(): State<List<ExchangeRate>> {
        val page by page.subscribeAsState()

        return exchangeRateRepository.selectAllPaginated((page - 1).toLong(), pageSize.value.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .collectAsState(emptyList())
    }

    data class UnsavedExchangeRate(
        val baseCurrency: Currency,
        val counterCurrency: Currency,
        val effectiveSince: LocalDateTime,
        val rate: Double,
    )
}
