/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import android.content.Context
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.Entry
import me.gustavolopezxyz.common.data.ExchangeRate
import me.gustavolopezxyz.common.data.MoneyTransaction

actual class DatabaseFactory(private val context: Context) {
    actual fun create(): Database {
        val instantColumnAdapter = InstantColumnAdapter()

        return Database(
            AndroidSqliteDriver(Database.Schema, context, "wimm.db"),
            accountAdapter = Account.Adapter(EnumColumnAdapter()),
            entryAdapter = Entry.Adapter(instantColumnAdapter),
            exchangeRateAdapter = ExchangeRate.Adapter(instantColumnAdapter),
            moneyTransactionAdapter = MoneyTransaction.Adapter(instantColumnAdapter),
        )
    }
}

