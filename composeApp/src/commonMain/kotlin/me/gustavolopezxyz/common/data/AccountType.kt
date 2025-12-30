/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

// Based on the accounting equation:
// Cash + Envelope + Asset = (Income - Expense)
enum class AccountType {
    Cash,
    Asset,
    Receivable,

    Payable,
    Expense,
    Income,

    // new model
    Normal,
    Debt,
    Envelope,
    ;

    val isAsset: Boolean
        get() = when (this) {
            Cash,
            Envelope,
            Asset -> true

            else -> false
        }

    val isLiquid: Boolean
        get() = this == Cash || this == Envelope

    companion object {
        val All: List<AccountType> get() = entries
        val Owned: List<AccountType> get() = entries.filter { it.isAsset }
    }
}
