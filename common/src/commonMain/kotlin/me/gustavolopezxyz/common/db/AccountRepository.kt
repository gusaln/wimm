/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Currency
import me.gustavolopezxyz.common.data.Database
import me.gustavolopezxyz.common.ext.toCurrency
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

    fun findById(id: Long): Account? {
        return accountQueries.selectById(listOf(id)).executeAsOneOrNull()
    }

    fun getByType(types: Collection<AccountType>): List<Account> {
        return accountQueries.selectByType(types).executeAsList()
    }

    fun getByType(type: AccountType): List<Account> {
        return accountQueries.selectByType(listOf(type)).executeAsList()
    }

    fun allAsFlow(): Flow<Query<Account>> {
        return accountQueries.selectAll().asFlow()
    }

    fun create(type: AccountType, name: String, currency: Currency) {
        return accountQueries.insertAccount(type, name, currency.toString())
    }

    fun update(original: Account, modified: Account) = update(
        original.accountId,
        modified.type,
        modified.name,
        modified.currency.toCurrency()
    )

    fun update(modified: Account) = update(
        modified.accountId,
        modified.type,
        modified.name,
        modified.currency.toCurrency()
    )

    private fun update(
        accountId: Long,
        type: AccountType,
        name: String,
        currency: Currency,
    ) {
        return accountQueries.updateAccount(
            type = type,
            name = name,
            currency = currency.code,
            accountId = accountId,
        )
    }

    fun recomputeBalance(account: Account) = recomputeBalance(account.accountId)

    fun recomputeBalance(accountId: Long) {
        accountQueries.recomputeBalanceOf(accountId)
    }

    fun recomputeBalances() {
        accountQueries.recomputeAllBalances()
    }
}