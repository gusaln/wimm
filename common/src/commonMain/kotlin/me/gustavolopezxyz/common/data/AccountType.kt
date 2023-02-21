/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

// Based on the accounting equation:
// Cash + Envelope + Asset + Receivable = Payable + (Income - Expense)
enum class AccountType {
    Cash,
    Envelope,
    Asset,
    Receivable,

    Payable,
    Expense,
    Income;


    companion object {
        val InAssets: List<AccountType> get() = listOf(Cash, Envelope, Asset, Receivable)
    }
}
