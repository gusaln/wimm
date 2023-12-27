/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.navigation

import app.cash.sqldelight.coroutines.mapToList
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.db.AccountRepository
import me.gustavolopezxyz.common.money.Currency
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class ManageAccountsComponent(
    componentContext: ComponentContext,
    override val di: DI,
    val onShowAccountSummary: (accountId: Long) -> Unit
) : DIAware, ComponentContext by componentContext {
    val accountRepository: AccountRepository by instance()

    fun getAccounts() = accountRepository.allAsFlow().mapToList(Dispatchers.IO)

    fun createAccount(name: String, type: AccountType, currency: Currency) {
        accountRepository.create(type, name, currency)
    }

    fun editAccount(modified: Account) {
        accountRepository.update(modified)
    }

    fun recomputeBalances() {
        accountRepository.recomputeBalances()
    }
}

