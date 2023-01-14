/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.gustavolopezxyz.common.Constants
import me.gustavolopezxyz.common.data.Money
import me.gustavolopezxyz.common.data.getCurrency
import me.gustavolopezxyz.common.ext.money
import me.gustavolopezxyz.common.ext.toSimpleFormat
import me.gustavolopezxyz.db.Account

data class ListEntryDto(
    val id: Long,
    val description: String,
    val account: Account,
    val amount: Money,
    val incurred_at: Instant,
    val recorded_at: Instant
)

@Composable
fun EntriesListCard(entry: ListEntryDto) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(Constants.Size.SMALL.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Details
                Column {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                MaterialTheme.typography.body1.toSpanStyle()
                            ) {
                                append(entry.description)
                                append(' ')
                            }

                            withStyle(
                                SpanStyle(
                                    color = Color.Gray, fontSize = MaterialTheme.typography.caption.fontSize
                                )
                            ) {
                                append("(${entry.account.name})")
                            }
                        }, overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        entry.incurred_at.toSimpleFormat(),
                        fontSize = MaterialTheme.typography.caption.fontSize
                    )
                }

                // Amount
                Column {
                    Text(
                        entry.amount.value.toString(),
                        modifier = Modifier.align(Alignment.End),
                        color = if (entry.amount.isNegative()) {
                            Color.Red
                        } else {
                            Color.Unspecified
                        },
                        fontSize = MaterialTheme.typography.body1.fontSize,
                    )

                    Text(
                        entry.amount.currency.toString(),
                        modifier = Modifier.align(Alignment.End),
                        color = Color.Gray,
                    )
                }
            }
        }
    }
}


@Composable
fun EntriesList(entries: List<ListEntryDto>, onPageUpdate: (Int) -> Unit, onLimitUpdate: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Constants.Size.MEDIUM.dp)
    ) {
        entries.forEach {
            EntriesListCard(it)
        }
    }
}

@Preview
@Composable
fun EntriesListPreview() {
    val now = Clock.System.now()

    val ac1 = Account(1, "Savings", "USD", 100.0, 0.0)
    val ac2 = Account(2, "Checking", "VES", 50.0, 0.0)

    EntriesList(listOf(
        ListEntryDto(1, "Cash", ac1, 100.0.money(ac1.getCurrency()), now, now),
        ListEntryDto(2, "Bonus", ac2, (-100.0).money(ac2.getCurrency()), now, now),
    ), {}, {})
}