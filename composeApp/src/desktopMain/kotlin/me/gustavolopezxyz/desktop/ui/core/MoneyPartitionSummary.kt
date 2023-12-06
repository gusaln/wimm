/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.desktop.ui.core

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import me.gustavolopezxyz.common.data.*
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.AppTheme
import me.gustavolopezxyz.desktop.ui.common.*
import kotlin.math.absoluteValue

internal val AccountPartitionSummaryDefaultColors = lazy {
    Palette.Colors.asIterable().filterNot { it.key.startsWith("gray") || it.key.startsWith("zinc") }
        .filter { it.key.endsWith("200") || it.key.endsWith("400") || it.key.endsWith("600") || it.key.endsWith("800") }
        .sortedByDescending { it.key }.map { it.value }
}

@Composable
fun MoneyPartitionSummary(
    title: String,
    currency: Currency,
    amounts: List<MoneyPartitionEntry>,
    fractionByCategory: Boolean = false,
    descending: Boolean = true,
    sortBy: ((MoneyPartitionEntry) -> Double)? = null,
    colorPalette: List<Color> = AccountPartitionSummaryDefaultColors.value,
    modifier: Modifier = Modifier,
) {
    MoneyPartitionSummary(
        currency = currency,
        amounts = amounts,
        descending = descending,
        fractionByCategory = fractionByCategory,
        sortBy = sortBy,
        colorPalette = colorPalette,
        modifier = modifier
    ) {
        AppListTitle(title)
    }
}

@Composable
fun MoneyPartitionSummary(
    currency: Currency,
    amounts: List<MoneyPartitionEntry>,
    fractionByCategory: Boolean = false,
    descending: Boolean = true,
    sortBy: ((MoneyPartitionEntry) -> Double)? = null,
    colorPalette: List<Color> = AccountPartitionSummaryDefaultColors.value,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit),
) {
    MoneyPartitionSummary(
        currency = currency,
        amounts = amounts,
        fractionByCategory = fractionByCategory,
        descending = descending,
        sortBy = sortBy,
        colorSelector = { index, _ -> colorPalette[index % colorPalette.size] },
        modifier = modifier
    ) {
        title()
    }
}

/**
 * @param name Unique name of the entry
 * @param category Categorizing property of the entry
 * @param amount Amount of the entry
 */
data class MoneyPartitionEntry(val name: String, val category: String, val amount: Double) {
    constructor(name: String, amount: Double) : this(name, name, amount)
}

@Composable
fun MoneyPartitionSummary(
    currency: Currency,
    amounts: List<MoneyPartitionEntry>,
    fractionByCategory: Boolean = false,
    descending: Boolean = true,
    sortBy: ((MoneyPartitionEntry) -> Double)? = null,
    colorSelector: (index: Int, category: String) -> Color,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit),
) {
    val amountsEntries by derivedStateOf {
        amounts.run {
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

    val total by derivedStateOf {
        amountsEntries.filter { included.getOrDefault(it.name, true) }.sumOf { it.amount }
    }
    val absoluteTotal by derivedStateOf {
        amountsEntries.filter { included.getOrDefault(it.name, true) }.sumOf { it.amount.absoluteValue }
    }
    val totalByCategory by derivedStateOf {
        buildMap<String, Double> {
            amounts.filter { included.getOrDefault(it.name, true) }.forEach {
                this[it.category] = this.getOrDefault(it.category, 0.0) + it.amount
            }
        }
    }
    var hoveredCategory by remember { mutableStateOf<String?>(null) }

    AppCard(modifier = modifier) {
        Column {
            title()

            // Top Summary
            Row {
                Row(Modifier.weight(1f), Arrangement.Center, Alignment.CenterVertically) {
                    MoneyText(
                        total,
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
                            val fraction by derivedStateOf {
                                if (!isIncluded) {
                                    0.0f
                                } else if (fractionByCategory) {
                                    (entry.amount / totalByCategory.getOrDefault(
                                        entry.category,
                                        1.0
                                    )).toFloat()
                                } else {
                                    (entry.amount / absoluteTotal).toFloat()
                                }
//
//                                if (isIncluded) (entry.amount / totalByCategory.getOrDefault(
//                                    entry.category,
//                                    1.0
//                                )).toFloat() else 0.0f
                            }

                            LaunchedEffect(Unit) {
                                interactionSource.interactions.collect {
                                    when (it) {
                                        is HoverInteraction.Enter -> hoveredCategory = entry.category
                                        is HoverInteraction.Exit -> if (hoveredCategory == entry.category) hoveredCategory =
                                            null

                                        else -> {}
                                    }
                                }
                            }

                            AppListItem(
                                modifier = Modifier.hoverable(interactionSource, isIncluded)
                            ) {
                                IconToggleButton(checked = isIncluded,
                                    onCheckedChange = { included[entry.name] = it }) {
                                    if (isIncluded) {
                                        Icon(Icons.Default.CheckBox, "checkbox")
                                    } else {
                                        Icon(Icons.Default.CheckBoxOutlineBlank, "blank checkbox")
                                    }
                                }

                                Text(
                                    text = entry.name,
                                    modifier = Modifier.weight(1f),
                                    textDecoration = if (isIncluded) null else TextDecoration.LineThrough
                                )

                                AppChip(
                                    color = colorSelector(index, entry.category),
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
                totalByCategory.entries.run {
//                    if (sortBy != null) {
//                        this.sortedBy
//                    }  else

                    if (descending) {
                        this.sortedByDescending { it.value }
                    } else {
                        this.sortedBy { it.value }
                    }
                }.forEachIndexed { index, entry ->
                    val fraction by derivedStateOf { (entry.value / absoluteTotal).absoluteValue.toFloat() }

                    Card(
                        modifier = Modifier.weight(fraction).fillMaxHeight().border(
                            2.dp,
                            if (hoveredCategory == entry.key) MaterialTheme.colorScheme.onBackground else Color.Unspecified
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = colorSelector(index, entry.key)
                        )
                    ) {

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
        MoneyPartitionSummary("Accounts", currencyOf("USD"), accounts.map { MoneyPartitionEntry(it.name, it.balance) })
    }
}