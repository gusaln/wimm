/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.ui.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import me.gustavolopezxyz.common.ext.currentTz
import me.gustavolopezxyz.common.ui.theme.AppDimensions
import me.gustavolopezxyz.common.ui.theme.titleMedium


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
fun FormTitle(title: String) {
    Text(title, style = MaterialTheme.typography.h5)
}

@Composable
fun CardTitle(
    modifier: Modifier = Modifier.heightIn(24.dp, 64.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit
) {
    ProvideTextStyle(MaterialTheme.typography.h6) {
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
internal val ListItemPrimaryTextStyle @Composable get() = MaterialTheme.typography.subtitle1
internal val ListItemSecondaryTextStyle @Composable get() = MaterialTheme.typography.caption.copy(color = Color.Gray)

@Composable
fun AppList(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium)) {
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
    ProvideTextStyle(MaterialTheme.typography.subtitle2) {
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
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.padding(ListItemHorizontalPadding, ListItemVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Default.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun AppListItem(
    modifier: Modifier = Modifier,
    secondaryText: @Composable ColumnScope.() -> Unit = {},
    action: @Composable RowScope.() -> Unit,
    text: @Composable ColumnScope.() -> Unit,
) {
    AppListItem(modifier = modifier) {
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
    Divider(color = MaterialTheme.colors.background, thickness = 1.dp, modifier = modifier)
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

@Preview
@Composable
fun OutlinedDateTextFieldPreview() {
    val date = Clock.System.now().toLocalDateTime(currentTz()).date

    OutlinedDateTextField(date, onValueChange = {})
}