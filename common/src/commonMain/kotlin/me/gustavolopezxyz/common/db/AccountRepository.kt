/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import me.gustavolopezxyz.common.data.Money
import me.gustavolopezxyz.common.data.getInitialBalance
import me.gustavolopezxyz.db.Account
import me.gustavolopezxyz.db.AccountQueries
import me.gustavolopezxyz.db.Database
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountRepository : KoinComponent {
    private val db: Database by inject()
    private val accountQueries: AccountQueries = db.accountQueries

    fun getAll(): List<Account> {
        return accountQueries.selectAll().executeAsList()
    }

    fun getById(ids: Collection<Long>): List<Account> {
        return accountQueries.selectById(ids).executeAsList()
    }

    fun findById(id: Long): Account {
        return accountQueries.selectById(listOf(id)).executeAsOne()
    }

    fun allAsFlow(): Flow<Query<Account>> {
        return accountQueries.selectAll().asFlow()
    }

    fun create(name: String, initialBalance: Money) {
        return accountQueries.insertAccount(name, initialBalance.currency.toString(), initialBalance.value)
    }

    private fun update(account: Account) {
        return accountQueries.updateAccount(
            name = account.name,
            balance_currency = account.balance_currency,
            balance_value = account.balance_value,
            initial_value = account.initial_value,
            id = account.id,
        )
    }

    fun updateName(account: Account, name: String) {
        return update(account.copy(name = name))
    }

    fun updateInitialBalance(account: Account, initialBalance: Money) {
        val delta = initialBalance - account.getInitialBalance()

        return update(
            account.copy(
                balance_value = account.balance_value + delta.value,
                initial_value = initialBalance.value,
            )
        )
    }
}