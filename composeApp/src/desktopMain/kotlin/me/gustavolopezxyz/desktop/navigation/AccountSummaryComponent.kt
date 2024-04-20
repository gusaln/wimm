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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import me.gustavolopezxyz.common.data.EntryForAccount
import me.gustavolopezxyz.common.data.toEntryForAccount
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.db.EntryRepository
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import kotlin.coroutines.CoroutineContext

class AccountSummaryComponent(
    componentContext: ComponentContext,
    override val di: DI,
    val accountId: Long,
    val onSelectEntry: (entry: EntryForAccount) -> Unit,
    val onNavigateBack: () -> Unit,
) : DIAware, ComponentContext by componentContext {
    private val accountRepository: AccountRepository by instance()
    private val entryRepository: EntryRepository by instance()

    val account by lazy {
        accountRepository.findByIdOrNull(accountId)
    }

    val page = MutableValue(1)
    fun onNextPage() {
        page.value++
    }

    fun onPrevPage() {
        page.value--
    }

    @Composable
    fun collectEntriesAsState(scope: CoroutineContext, perPage: Int): State<List<EntryForAccount>> {
        val page by page.subscribeAsState()

        return entryRepository.getAllForAccountAsFlow(accountId, ((page - 1) * perPage).toLong(), perPage.toLong())
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toEntryForAccount() } }
            .collectAsState(emptyList(), scope)
    }
}