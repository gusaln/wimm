/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

enum class AccountType {
    Cash,
    Receivable,

    Payable,
    Cost,
    Income;

    fun isDebit(): Boolean = when (this) {
        Cash,
        Receivable,
        Cost -> true

        Payable,
        Income -> false
    }

    fun isCredit(): Boolean = !isDebit()
}
