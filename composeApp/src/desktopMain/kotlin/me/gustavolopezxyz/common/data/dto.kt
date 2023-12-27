/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.data

import androidx.compose.runtime.Immutable
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import me.gustavolopezxyz.common.db.SelectEntriesForTransaction
import me.gustavolopezxyz.common.ext.datetime.currentTimeZone
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.common.money.Currency
import me.gustavolopezxyz.common.money.MissingCurrency
import me.gustavolopezxyz.common.money.Money
import java.util.*

internal fun newEntryId(): Long {
//    val id: Long = -Clock.System.now().toEpochMilliseconds(),
    return -UUID.randomUUID().leastSignificantBits
}

internal fun currentDate(tz: TimeZone = currentTimeZone()) = Clock.System.now().toLocalDateTime(tz).date

@Immutable
data class NewEntryDto(
    val id: Long = newEntryId(),
    val accountId: Long? = null,
    val amount: Money = Money(MissingCurrency, 0.0),
    val recordedAt: LocalDate = currentDate(),
    val reference: String? = null,
) {
    inline val amountCurrency: Currency
        get() = amount.currency

    inline val amountValue: Double
        get() = amount.value

    constructor(
        id: Long = newEntryId(),
        account: Account? = null,
        amount: Double = 0.0,
        recordedAt: LocalDate?,
        reference: String? = null,
    ) : this(
        id,
        account?.accountId,
        amount.toMoney(account?.currency?.toCurrency() ?: MissingCurrency),
        recordedAt ?: currentDate(),
        reference
    )
}

fun emptyNewEntryDto(recordedAt: LocalDate? = null) = NewEntryDto(recordedAt = recordedAt)


@Immutable
data class ModifiedEntryDto(
    val id: Long,
    val accountId: Long,
    val accountName: String,
    val amount: Money,
    val recordedAt: LocalDate,
    val reference: String? = null,
    val wasEdited: Boolean = false,
    val toDelete: Boolean = false,
) {

    constructor(
        id: Long,
        accountId: Long,
        accountName: String,
        currencyCode: String,
        amount: Double,
        recordedAt: LocalDate,
        reference: String? = null,
        wasEdited: Boolean = false,
        toDelete: Boolean = false,
    ) : this(
        id,
        accountId,
        accountName,
        amount.toMoney(currencyCode),
        recordedAt,
        reference,
        wasEdited,
        toDelete
    )

    inline val amountCurrency: Currency
        get() = amount.currency

    inline val amountCurrencyCode: String
        get() = amount.currency.code

    inline val amountValue: Double
        get() = amount.value

    fun edit(
        amount: Double = this.amount.value,
        currency: Currency = this.amount.currency,
        recordedAt: LocalDate = this.recordedAt,
        reference: String? = this.reference,
    ) = copy(
        amount = amount.toMoney(currency),
        recordedAt = recordedAt,
        reference = reference,
        wasEdited = true,
        toDelete = false,
    )

    fun changeAccount(account: Account) = copy(
        accountId = account.accountId,
        accountName = account.name,
        amount = this.amount.withCurrency(account.currency),
        wasEdited = true,
        toDelete = false,
    )

    fun delete() = copy(toDelete = true)

    fun restore() = copy(toDelete = false)

    fun toEntry(transactionId: Long): Entry = Entry(
        entryId = id,
        transactionId = transactionId,
        accountId = accountId,
        amount = amount.value,
        recordedAt = recordedAt.atTime(0, 0, 0).toInstant(
            currentTimeZone()
        ),
        reference = reference
    )
}

fun modifiedEntryDto(entry: SelectEntriesForTransaction): ModifiedEntryDto {
    return ModifiedEntryDto(
        id = entry.entryId,
        accountId = entry.accountId,
        accountName = entry.accountName,
        amount = entry.amount.toMoney(entry.currency),
        recordedAt = entry.recordedAt.toLocalDateTime(currentTimeZone()).date,
        reference = entry.reference
    )
}