/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.core

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.ui.common.*
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.AppTheme

internal val AccountPartitionSummaryDefaultColors = lazy {
    Palette.Colors
        .asIterable()
        .filterNot { it.key.startsWith("gray") || it.key.startsWith("zinc") }
        .filter { it.key.endsWith("200") || it.key.endsWith("400") || it.key.endsWith("600") || it.key.endsWith("800") }
        .sortedByDescending { it.key }
        .map { it.value }
}

@Composable
fun MoneyPartitionSummary(
    title: String,
    currency: Currency,
    amounts: Map<String, Double>,
    descending: Boolean = true,
    sortBy: ((MoneyPartitionEntry) -> Double)? = null,
    colorPalette: List<Color> = AccountPartitionSummaryDefaultColors.value,
    modifier: Modifier = Modifier,
) {
    MoneyPartitionSummary(
        currency = currency,
        amounts = amounts,
        descending = descending,
        sortBy = sortBy,
        colorPalette = colorPalette,
        modifier = modifier
    ) {
        AppListTitle(title)
    }
}

data class MoneyPartitionEntry(val name: String, val amount: Double)

@Composable
fun MoneyPartitionSummary(
    currency: Currency,
    amounts: Map<String, Double>,
    descending: Boolean = true,
    sortBy: ((MoneyPartitionEntry) -> Double)? = null,
    colorPalette: List<Color> = AccountPartitionSummaryDefaultColors.value,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit),
) {
    val amountsEntries by derivedStateOf {
        amounts.map { MoneyPartitionEntry(it.key, it.value) }.run {
            if (sortBy != null) {
                this.sortedBy(sortBy)
            } else if (descending) {
                this.sortedByDescending { it.amount }
            } else {
                this.sortedBy { it.amount }
            }
        }
    }

    val included = remember { mutableStateMapOf<String, Boolean>() }
    val filteredTotal by derivedStateOf {
        amountsEntries.asIterable().filter { included.getOrDefault(it.name, true) }.sumOf { it.amount }
    }
    var hoveredName by remember { mutableStateOf<String?>(null) }

    AppCard(modifier = modifier) {
        Column {
            title()

            // Top Summary
            Row {
                Row(Modifier.weight(1f), Arrangement.Center, Alignment.CenterVertically) {
                    MoneyText(
                        filteredTotal,
                        currencyOf("USD"),
                        Modifier.fillMaxWidth().align(Alignment.CenterVertically),
                        commonStyle = TextStyle(fontSize = 2.5.em)
                    )
                }

                Spacer(Modifier.width(AppDimensions.Default.spacing.medium))

                Column(Modifier.weight(2f)) {
                    AppList {
                        amountsEntries.forEachIndexed { index, entry ->
                            val interactionSource = remember { MutableInteractionSource() }

                            val isIncluded by derivedStateOf { included.getOrDefault(entry.name, true) }
                            val fraction by derivedStateOf { if (isIncluded) (entry.amount / filteredTotal).toFloat() else 0.0f }

                            LaunchedEffect(Unit) {
                                interactionSource.interactions.collect {
                                    when (it) {
                                        is HoverInteraction.Enter -> hoveredName = entry.name
                                        is HoverInteraction.Exit -> if (hoveredName == entry.name) hoveredName = null
                                        else -> {}
                                    }
                                }
                            }

                            AppListItem(
                                modifier = Modifier
                                    .toggleable(isIncluded, onValueChange = { included[entry.name] = it })
                                    .hoverable(interactionSource, isIncluded)
                            ) {
                                Text(
                                    text = entry.name,
                                    modifier = Modifier.weight(1f),
                                    textDecoration = if (isIncluded) null else TextDecoration.LineThrough
                                )

                                AppChip(
                                    color = colorPalette[index % colorPalette.size],
                                    modifier = Modifier.width(80.dp),
                                ) {
                                    Text(
                                        text = PercentageFormatter.format(fraction),
                                        style = TextStyle(color = Color.Black)
                                    )
                                }

                                Spacer(Modifier.width(18.dp))

                                MoneyText(entry.amount, currency, Modifier.width(110.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Graphic
            Row(Modifier.fillMaxWidth().height(10.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                amountsEntries.forEachIndexed { index, acc ->
                    val isIncluded by derivedStateOf { included.getOrDefault(acc.name, true) }
                    val fraction by derivedStateOf { (acc.amount / filteredTotal).toFloat() }

                    if (isIncluded) {
                        Card(
                            Modifier.weight(fraction).fillMaxHeight().border(
                                2.dp,
                                if (hoveredName == acc.name) MaterialTheme.colors.onBackground else Color.Unspecified
                            ),
                            backgroundColor = colorPalette[index % colorPalette.size]
                        ) {
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MoneyPartitionSummaryPreview() {
    val accounts = listOf(
        Account(1, AccountType.Cash, "Bank 1", "USD", 100.0),
        Account(2, AccountType.Cash, "Bank 2", "USD", 10.0),
        Account(3, AccountType.Cash, "Bank 3", "USD", 20.0),
        Account(4, AccountType.Cash, "Bank 4", "USD", 25.0),
        Account(5, AccountType.Cash, "Bank 5", "USD", 45.0),
    )

    AppTheme {
        MoneyPartitionSummary("Accounts", currencyOf("USD"), accounts.associateBy({ it.name }, { it.balance }))
    }
}