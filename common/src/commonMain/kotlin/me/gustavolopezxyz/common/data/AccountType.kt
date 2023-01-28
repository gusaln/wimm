/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

enum class AccountType {
    Cash,
    Asset,
    Receivable,

    Payable,
    Expense,
    Income;

    fun isDebit(): Boolean = when (this) {
        Cash,
        Asset,
        Receivable,
        Expense -> true

        Payable,
        Income -> false
    }

    fun isCredit(): Boolean = !isDebit()
}
