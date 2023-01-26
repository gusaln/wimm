package me.gustavolopezxyz.common.ui

import kotlinx.datetime.*
import me.gustavolopezxyz.common.ext.currentTz
import me.gustavolopezxyz.common.ext.getRandomString
import me.gustavolopezxyz.db.Account
import me.gustavolopezxyz.db.Entry
import me.gustavolopezxyz.db.SelectEntriesFromRecord

data class NewEntryDto(
    val uid: String = getRandomString(8),
    val description: String = "",
    val account: Account? = null,
    val amount: Double = 0.0,
    val incurred_at: LocalDate,
    val recorded_at: LocalDate = incurred_at
)

fun makeEmptyNewEntryDto() = NewEntryDto(incurred_at = Clock.System.now().toLocalDateTime(currentTz()).date)


data class EditEntryDto(
    val id: Long,
    val account_id: Long,
    val account_name: String,
    val account_currency: String,
    val description: String,
    val amount: Double,
    val incurred_at: LocalDate,
    val recorded_at: LocalDate,
    val edited: Boolean = false,
    val to_delete: Boolean = false,
) {
    fun edit(
        description: String = this.description,
        amount: Double = this.amount,
        incurred_at: LocalDate = this.incurred_at,
        recorded_at: LocalDate = this.recorded_at,
    ) = copy(
        description = description,
        amount = amount,
        incurred_at = incurred_at,
        recorded_at = recorded_at,
        edited = true,
        to_delete = false,
    )

    fun changeAccount(account: Account) = copy(
        account_id = account.id,
        account_name = account.name,
        account_currency = account.balance_currency,
        edited = true,
        to_delete = false,
    )

    fun delete() = copy(to_delete = true)

    fun restore() = copy(to_delete = false)

    fun toEntry(recordId: Long): Entry = Entry(
        id,
        account_id = account_id,
        record_id = recordId,
        description = description,
        amount_currency = account_currency,
        amount_value = amount,
        incurred_at = incurred_at.atTime(0, 0, 0).toInstant(
            currentTz()
        ),
        recorded_at = recorded_at.atTime(0, 0, 0).toInstant(
            currentTz()
        )
    )
}

fun makeEditEntryDtoFrom(entry: Entry, account: Account): EditEntryDto {
    return EditEntryDto(
        id = entry.id,
        account_id = account.id,
        account_name = account.name,
        account_currency = account.balance_currency,
        description = entry.description,
        amount = entry.amount_value,
        incurred_at = entry.incurred_at.toLocalDateTime(currentTz()).date,
        recorded_at = entry.recorded_at.toLocalDateTime(currentTz()).date,
    )
}

fun makeEditEntryDtoFrom(entry: SelectEntriesFromRecord): EditEntryDto {
    return EditEntryDto(
        id = entry.id,
        account_id = entry.account_id,
        account_name = entry.account_name,
        account_currency = entry.account_currency,
        description = entry.description,
        amount = entry.amount_value,
        incurred_at = entry.incurred_at.toLocalDateTime(currentTz()).date,
        recorded_at = entry.recorded_at.toLocalDateTime(currentTz()).date,
    )
}