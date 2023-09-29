/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import java.math.RoundingMode
import java.text.DecimalFormat


fun Int.minMax(min: Int, max: Int) = when {
    this < min -> min
    this > max -> max
    else -> this
}

@Composable
fun ScreenTitle(content: @Composable RowScope.() -> Unit) {
    ProvideTextStyle(MaterialTheme.typography.titleMedium) {
        Row(modifier = Modifier.padding(bottom = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            content()
        }
    }
}

@Composable
fun ScreenTitle(title: String) {
    ScreenTitle { Text(title) }
}

@Composable
fun FormTitle(title: String) {
    Text(title, style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun CardTitle(
    modifier: Modifier = Modifier.heightIn(24.dp, 64.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    ProvideTextStyle(MaterialTheme.typography.titleLarge) {
        Row(modifier, horizontalArrangement, verticalAlignment) {
            content()
        }
    }
}

@Composable
fun AppButton(onClick: () -> Unit, text: String, colors: ButtonColors = ButtonDefaults.buttonColors()) {
    Button(onClick, colors = colors) {
        Text(text.uppercase())
    }
}

@Composable
fun AppTextButton(onClick: () -> Unit, text: String, colors: ButtonColors = ButtonDefaults.textButtonColors()) {
    TextButton(onClick, colors = colors) {
        Text(text.uppercase())
    }
}

@Composable
fun AppTextButton(
    onClick: () -> Unit,
    text: String,
    icon: @Composable () -> Unit,
    colors: ButtonColors = ButtonDefaults.textButtonColors()
) {
    TextButton(onClick, colors = colors) {
        icon()

        Spacer(modifier = Modifier.width(AppDimensions.Default.padding.large))

        Text(text.uppercase())
    }
}

internal val ListItemHorizontalPadding = 16.dp
internal val ListItemVerticalPadding = 4.dp
internal val ListItemPrimaryAndSecondaryTextSpace = 8.dp
internal val ListItemPrimaryTextStyle @Composable get() = MaterialTheme.typography.bodyLarge
internal val ListItemSecondaryTextStyle @Composable get() = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Card(modifier) {
        Box(Modifier.padding(AppDimensions.Default.cardPadding)) {
            content()
        }
    }
}

@Composable
fun AppChip(
    color: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
    text: @Composable RowScope.() -> Unit
) {
    Surface(
        color = color,
        contentColor = contentColorFor(color),
        shape = MaterialTheme.shapes.small
    ) {
        Row(modifier.padding(12.dp, 0.dp)) {
            text()
        }
    }
}

@Composable
fun AppList(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AppDimensions.Default.listSpaceBetween),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier, verticalArrangement = verticalArrangement) {
        content()
    }
}

@Composable
fun ListItemSpacer() {
    Spacer(Modifier.height(AppDimensions.Default.listSpaceBetween))
}

@Composable
fun AppLazyList(
    modifier: Modifier = Modifier,
    spaceBetween: Dp = AppDimensions.Default.listSpaceBetween,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    LazyColumn(modifier, state, verticalArrangement = Arrangement.spacedBy(spaceBetween)) {
        content()
    }
}

@Composable
fun AppListTitle(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    ProvideTextStyle(MaterialTheme.typography.titleSmall) {
        Row(modifier.padding(bottom = 8.dp), horizontalArrangement, verticalAlignment) {
            content()
        }
    }
}

@Composable
fun AppListTitle(
    title: String,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    AppListTitle(modifier, horizontalArrangement, verticalAlignment) { Text(title) }
}

@Composable
fun AppListItem(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = ListItemHorizontalPadding,
    verticalPadding: Dp = ListItemVerticalPadding,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.padding(horizontalPadding, verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun AppListItem(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = ListItemHorizontalPadding,
    verticalPadding: Dp = ListItemVerticalPadding,
    secondaryText: @Composable ColumnScope.() -> Unit = {},
    action: @Composable RowScope.() -> Unit,
    text: @Composable ColumnScope.() -> Unit,
) {
    AppListItem(modifier = modifier, horizontalPadding, verticalPadding) {
        Column(Modifier.weight(1f)) {
            ProvideTextStyle(ListItemPrimaryTextStyle) {
                text()
            }
            Spacer(Modifier.height(ListItemPrimaryAndSecondaryTextSpace))
            ProvideTextStyle(ListItemSecondaryTextStyle) {
                secondaryText()
            }
        }

        action()
    }
}

@Composable
fun AppDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colorScheme.background, thickness = 1.dp, modifier = modifier)
}

@Composable
fun AppCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked, onCheckedChange, enabled = enabled)

        Text(label)
    }
}

val PercentageFormatter = DecimalFormat("#0.0 %")
val NumberFormatter = DecimalFormat("#,##0.00").apply {
    roundingMode = RoundingMode.CEILING
}

@Composable
fun OutlinedDateTextField(
    date: LocalDate,
    onValueChange: (date: LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
    var dateString by remember { mutableStateOf(date.toString().replace("-", "/")) }

    OutlinedTextField(
        dateString, onValueChange = { raw ->
            val parts = raw.filter { c -> c.isDigit() || c == '/' }.split('/')

            val year = parts[0].take(4).padStart(1, '0').toInt()
            val month = parts[1].take(2).padStart(1, '0').toInt().minMax(1, 12)
            val dayOfMonth = parts[2].take(2).padStart(1, '0').toInt().minMax(
                min = 1, max = when (month) {
                    2 -> 27
                    4, 6, 9, 11 -> 30
                    else -> 31
                }
            )

            onValueChange(LocalDate(year, month, dayOfMonth))

            dateString = "%04d/%02d/%02d".format(year, month, dayOfMonth)
        }, modifier = modifier, label = label, placeholder = placeholder, singleLine = true, readOnly = readOnly
    )
}

@Composable
fun OutlinedDoubleField(
    value: Double,
    onValueChange: (Double) -> Unit,
    decimals: Int = 2,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = "%.2f".format(value),
        onValueChange = { raw ->
            val sign = if (raw.contains('-')) "-" else ""
            val minorUnit = raw.filter { c -> c.isDigit() }.padStart(decimals + 2, '0')
            val before = minorUnit.substring(0, minorUnit.length - decimals).padEnd(1, '0')
            val after = minorUnit.substring(minorUnit.length - decimals).padEnd(decimals, '0')

            onValueChange("$sign$before.$after".toDouble())
        },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        singleLine = true,
        readOnly = readOnly,
    )
}
