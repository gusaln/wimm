/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.Money
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.ext.toMoney
import me.gustavolopezxyz.common.ext.toSimpleFormat

data class ListEntryDto(
    val id: Long,
    val description: String,
    val record_id: Long,
    val account: Account,
    val amount: Money,
    val incurred_at: Instant,
    val recorded_at: Instant
)

@Composable
fun EntriesListCard(entry: ListEntryDto, onEdit: (ListEntryDto) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onEdit(entry) }, elevation = 2.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.Small.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Details
                Column {
                    EntrySummaryText(entry.description, entry.account.name)

                    Text(
                        entry.incurred_at.toSimpleFormat(), fontSize = MaterialTheme.typography.caption.fontSize
                    )
                }

                // Amount
                MoneyText(entry.amount.value, entry.amount.currency)
            }
        }
    }
}

@Composable
fun EntriesList(entries: List<ListEntryDto>, onEdit: (ListEntryDto) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.Medium.dp)
    ) {
        entries.forEach {
            EntriesListCard(it, onEdit)
        }
    }
}

@Preview
@Composable
fun EntriesListPreview() {
    val now = Clock.System.now()

    val ac1 = Account(1, AccountType.Cash, "Savings", "USD", 100.0, 0.0)
    val ac2 = Account(2, AccountType.Cash, "Checking", "VES", 50.0, 0.0)

    EntriesList(
        listOf(
            ListEntryDto(1, "Cash", 1, ac1, 100.0.toMoney(ac1.getCurrency()), now, now),
            ListEntryDto(2, "Bonus", 1, ac2, (100.0).toMoney(ac2.getCurrency()), now, now),
            ListEntryDto(3, "Stuff", 1, ac2, (-10.0).toMoney(ac2.getCurrency()), now, now),
        )
    ) {}
}