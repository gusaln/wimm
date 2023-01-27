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
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Account
import me.gustavolopezxyz.common.data.AccountType
import me.gustavolopezxyz.common.data.DenormalizedEntry
import me.gustavolopezxyz.common.ext.toCurrency
import me.gustavolopezxyz.common.ext.toSimpleFormat

@Composable
fun EntriesListCard(entry: DenormalizedEntry, onEdit: (DenormalizedEntry) -> Unit) {
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
                    EntrySummaryText(entry.transactionDescription, entry.accountName)

                    Text(
                        entry.incurredAt.toSimpleFormat(), fontSize = MaterialTheme.typography.caption.fontSize
                    )
                }

                // Amount
                MoneyText(entry.amount, entry.currency.toCurrency())
            }
        }
    }
}

@Composable
fun EntriesList(entries: List<DenormalizedEntry>, onEdit: (DenormalizedEntry) -> Unit) {
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

    val ac1 = Account(1, AccountType.Cash, "Savings", "USD", 100.0)
    val ac2 = Account(2, AccountType.Cash, "Checking", "VES", 50.0)

    EntriesList(
        listOf(
            DenormalizedEntry(
                1,
                1,
                "Cash",
                1,
                ac1.name,
                "USD",
                100.0,
                now,
                now
            ),
            DenormalizedEntry(
                2,
                1,
                "Bonus",
                1,
                ac2.name,
                "USD",
                (100.0),
                now,
                now
            ),
            DenormalizedEntry(
                3,
                1,
                "Stuff",
                1,
                ac2.name,
                "USD",
                (-10.0),
                now,
                now
            ),
        )
    ) {}
}