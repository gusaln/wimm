package me.gustavolopezxyz.common.ui

import kotlinx.datetime.*
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.Entry
import me.gustavolopezxyz.common.ext.currentTz
import me.gustavolopezxyz.common.ext.getRandomString
import me.gustavolopezxyz.db.SelectEntriesFromTransaction

data class NewEntryDto(
    val uid: String = getRandomString(8),
    val account: Account? = null,
    val amount: Double = 0.0,
    val incurredAt: LocalDate,
    val recordedAt: LocalDate = incurredAt
)

fun makeEmptyNewEntryDto() = NewEntryDto(incurredAt = Clock.System.now().toLocalDateTime(currentTz()).date)


data class EditEntryDto(
    val id: Long,
    val accountId: Long,
    val accountName: String,
    val currency: String,
    val amount: Double,
    val incurredAt: LocalDate,
    val recordedAt: LocalDate,
    val wasEdited: Boolean = false,
    val toDelete: Boolean = false,
) {
    fun edit(
        amount: Double = this.amount,
        incurredAt: LocalDate = this.incurredAt,
        recordedAt: LocalDate = this.recordedAt,
    ) = copy(
        amount = amount,
        incurredAt = incurredAt,
        recordedAt = recordedAt,
        wasEdited = true,
        toDelete = false,
    )

    fun changeAccount(account: Account) = copy(
        accountId = account.accountId,
        accountName = account.name,
        currency = account.currency,
        wasEdited = true,
        toDelete = false,
    )

    fun delete() = copy(toDelete = true)

    fun restore() = copy(toDelete = false)

    fun toEntry(transactionId: Long): Entry = Entry(
        entryId = id,
        transactionId = transactionId,
        accountId = accountId,
        amount = amount,
        incurredAt = incurredAt.atTime(0, 0, 0).toInstant(
            currentTz()
        ),
        recordedAt = recordedAt.atTime(0, 0, 0).toInstant(
            currentTz()
        )
    )
}

fun makeEditEntryDtoFrom(entry: SelectEntriesFromTransaction): EditEntryDto {
    return EditEntryDto(
        id = entry.entryId,
        accountId = entry.accountId,
        accountName = entry.accountName,
        currency = entry.currency,
        amount = entry.amount,
        incurredAt = entry.incurredAt.toLocalDateTime(currentTz()).date,
        recordedAt = entry.recordedAt.toLocalDateTime(currentTz()).date,
    )
}