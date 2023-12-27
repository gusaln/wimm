/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.money.Currency

class AccountRepository(private val db: Database) {
    private val accountQueries: AccountQueries = db.accountQueries

    fun getAll(): List<Account> {
        return accountQueries.selectAll().executeAsList()
    }

    fun getById(ids: Collection<Long>): List<Account> {
        return accountQueries.selectById(ids).executeAsList()
    }

    fun findByIdOrNull(id: Long): Account? {
        return accountQueries.selectById(listOf(id)).executeAsOneOrNull()
    }

    fun findById(id: Long): Account {
        return accountQueries.selectById(listOf(id)).executeAsOne()
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