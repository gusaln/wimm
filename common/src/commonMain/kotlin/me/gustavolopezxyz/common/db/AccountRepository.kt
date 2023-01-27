/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.data.Money
import me.gustavolopezxyz.db.AccountQueries
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

    fun create(type: AccountType, name: String, initialBalance: Money) {
        return accountQueries.insertAccount(type, name, initialBalance.currency.toString(), initialBalance.value)
    }

    fun update(original: Account, modified: Account) = update(
        original.id,
        modified.type,
        modified.name,
        modified.balance_currency,
        modified.initial_value - original.initial_value,
    )

    private fun update(
        accountId: Long,
        type: AccountType,
        name: String,
        currency: String,
        initialBalanceDelta: Double,
    ) {
        return accountQueries.updateAccount(
            type = type,
            name = name,
            balance_currency = currency,
            initial_balance_delta = initialBalanceDelta,
            id = accountId,
        )
    }

}